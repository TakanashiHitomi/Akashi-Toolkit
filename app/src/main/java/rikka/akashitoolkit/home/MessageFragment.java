package rikka.akashitoolkit.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rikka.akashitoolkit.BuildConfig;
import rikka.akashitoolkit.R;
import rikka.akashitoolkit.adapter.Listener;
import rikka.akashitoolkit.model.CheckUpdate;
import rikka.akashitoolkit.model.MessageReadStatus;
import rikka.akashitoolkit.network.RetrofitAPI;
import rikka.akashitoolkit.otto.BusProvider;
import rikka.akashitoolkit.otto.PreferenceChangedAction;
import rikka.akashitoolkit.otto.ReadStatusResetAction;
import rikka.akashitoolkit.support.Settings;
import rikka.akashitoolkit.support.StaticData;
import rikka.akashitoolkit.utils.FileUtils;
import rikka.akashitoolkit.utils.FlavorsUtils;
import rikka.akashitoolkit.utils.NetworkUtils;

import static rikka.akashitoolkit.support.ApiConstParam.Message.COUNT_DOWN;

/**
 * Created by Rikka on 2016/6/11.
 */
public class MessageFragment extends BaseRefreshFragment<CheckUpdate> {

    private MessageAdapter mAdapter;

    private MessageReadStatus mMessageReadStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.instance().register(this);
        setHasOptionsMenu(true);

        loadReadStatus();
    }

    @Override
    public void onStop() {
        saveReadStatus();
        super.onStop();
    }

    private void loadReadStatus() {
        File f = new File(getContext().getCacheDir().getAbsoluteFile() + "/json/messages_read_status.json");
        if (f.exists()) {
            try {
                mMessageReadStatus = new Gson().fromJson(new FileReader(f), MessageReadStatus.class);
            } catch (FileNotFoundException ignored) {
            }
        }
        if (mMessageReadStatus == null) {
            mMessageReadStatus = new MessageReadStatus();
        }
    }

    private void saveReadStatus() {
        mMessageReadStatus.clearReadIdNotExisted();

        String json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mMessageReadStatus);
        FileUtils.saveStreamToCacheFile(getContext(),
                new ByteArrayInputStream(json.getBytes()),
                "/json/messages_read_status.json");
    }

    @Override
    public void onDestroy() {
        BusProvider.instance().unregister(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_twitter_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.windowBackground));
        mAdapter = new MessageAdapter();
        mRecyclerView.setAdapter(mAdapter);

        GridRecyclerViewHelper.init(mRecyclerView);

        mAdapter.setListener(new Listener() {
            @Override
            public void OnRemove(int position, Object data) {
                switch (mAdapter.getItemViewType(position)) {
                    case MessageAdapter.TYPE_MESSAGE:
                        mMessageReadStatus.addReadId(mAdapter.getItemId(position));
                        break;
                    case MessageAdapter.TYPE_MESSAGE_UPDATE:
                        mMessageReadStatus.setVersionCode(((CheckUpdate.UpdateEntity) data).getVersionCode());
                        break;
                    case MessageAdapter.TYPE_PUSH_INTRO:
                        mMessageReadStatus.setShowPushIntro(false);
                        break;
                }

            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
    }

    @Override
    public void onSuccess(@NonNull CheckUpdate data) {
        mMessageReadStatus.clearId();
        mAdapter.clearItemList();

        addLocalCard();
        addUpdateCard(data.getUpdate());
        addMessageCard(data.getMessages());

        /*checkDataUpdate(data.getData());*/

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Call<CheckUpdate> call, Throwable t) {

    }

    private void addLocalCard() {
        mAdapter.addItem(MessageAdapter.TYPE_DAILY_EQUIP, null, 0);
        mAdapter.addItem(MessageAdapter.TYPE_EXPEDITION_NOTIFY, null, 0);
        if (mMessageReadStatus.showPushIntro()) {
            mAdapter.addItem(MessageAdapter.TYPE_PUSH_INTRO, null, 0);
        }
    }

    private void addUpdateCard(final CheckUpdate.UpdateEntity entity) {
        if (FlavorsUtils.isPlay() && !BuildConfig.DEBUG) {
            return;
        }

        if (entity == null) {
            return;
        }

        int versionCode = StaticData.instance(getContext()).versionCode;

        if (versionCode <= mMessageReadStatus.getVersionCode()) {
            return;
        }

        if (entity.getVersionCode() > versionCode || BuildConfig.DEBUG) {
            mAdapter.addItem(MessageAdapter.TYPE_MESSAGE_UPDATE, entity, 0);
        }
    }

    private void addMessageCard(List<CheckUpdate.MessagesEntity> list) {
        if (list == null) {
            return;
        }

        for (final CheckUpdate.MessagesEntity entity :
                list) {
            // do not add card should not show
            if (!entity.shouldShow()) {
                continue;
            }

            // do not add card that time is early than now
            if ((entity.getType() & COUNT_DOWN) > 0) {
                if (entity.getTime() * DateUtils.SECOND_IN_MILLIS < System.currentTimeMillis()) {
                    continue;
                }
            }

            mMessageReadStatus.addId(entity.getId());

            // skip message that read
            if (mMessageReadStatus.isIdRead(entity.getId())) {
                continue;
            }

            if (entity.isShowFirst()) {
                mAdapter.addItem(MessageAdapter.TYPE_MESSAGE, entity, 0);
            } else {
                mAdapter.addItem(MessageAdapter.TYPE_MESSAGE, entity, -1);
            }
        }
    }

    @Override
    public void onRefresh(Call<CheckUpdate> call, boolean force_cache) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(NetworkUtils.getClient(force_cache))
                .baseUrl("http://app.kcwiki.moe/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        int channel = Settings
                .instance(getActivity())
                .getIntFromString(Settings.UPDATE_CHECK_CHANNEL, 0);

        RetrofitAPI.UpdateAPI service = retrofit.create(RetrofitAPI.UpdateAPI.class);
        call = service.get(CheckUpdate.API_VERSION, channel);

        super.onRefresh(call, force_cache);
    }

    @Subscribe
    public void readStatusReset(ReadStatusResetAction action) {
        mMessageReadStatus = new MessageReadStatus();

        onRefresh(true);

        Toast.makeText(getActivity(), R.string.read_status_reset, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void preferenceChanged(PreferenceChangedAction action) {
        switch (action.getKey()) {
            case Settings.UPDATE_CHECK_CHANNEL:
                if (mMessageReadStatus != null) {
                    mMessageReadStatus.setVersionCode(0);
                }

                onRefresh(true);
                break;
            case Settings.TWITTER_GRID_LAYOUT:
                GridRecyclerViewHelper.init(mRecyclerView);
                break;
        }
    }
}
