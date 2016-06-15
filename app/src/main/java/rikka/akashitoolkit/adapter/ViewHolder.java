package rikka.akashitoolkit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import rikka.akashitoolkit.R;
import rikka.akashitoolkit.model.Equip;
import rikka.akashitoolkit.model.EquipImprovement;
import rikka.akashitoolkit.otto.BusProvider;
import rikka.akashitoolkit.otto.ChangeNavigationDrawerItemAction;
import rikka.akashitoolkit.staticdata.EquipImprovementList;
import rikka.akashitoolkit.staticdata.EquipList;
import rikka.akashitoolkit.staticdata.EquipTypeList;
import rikka.akashitoolkit.support.Settings;
import rikka.akashitoolkit.ui.EquipDisplayActivity;
import rikka.akashitoolkit.ui.ImageDisplayActivity;
import rikka.akashitoolkit.ui.widget.ExpandableLayout;
import rikka.akashitoolkit.utils.MySpannableFactory;
import rikka.akashitoolkit.utils.Utils;

/**
 * Created by Rikka on 2016/3/12.
 */
public class ViewHolder {
    public static class Quest extends RecyclerView.ViewHolder {
        protected ExpandableLayout mExpandableLayout;
        protected TextView mName;
        protected TextView mDetail;
        protected TextView mNote;
        protected TextView mRewardText[] = new TextView[5];
        protected TextView mType[] = new TextView[2];
        protected LinearLayout mQuestContainer;


        public Quest(View itemView) {
            super(itemView);

            mExpandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandableLinearLayout);

            mType[0] = (TextView) itemView.findViewById(R.id.text_quest_type);
            mType[1] = (TextView) itemView.findViewById(R.id.text_quest_type2);
            mName = (TextView) itemView.findViewById(R.id.text_card_title);

            mDetail = (TextView) itemView.findViewById(R.id.text_quest_detail);
            mNote = (TextView) itemView.findViewById(R.id.text_quest_note);
            mRewardText[0] = (TextView) itemView.findViewById(R.id.text_number_0);
            mRewardText[1] = (TextView) itemView.findViewById(R.id.text_number_1);
            mRewardText[2] = (TextView) itemView.findViewById(R.id.text_number_2);
            mRewardText[3] = (TextView) itemView.findViewById(R.id.text_number_3);
            mRewardText[4] = (TextView) itemView.findViewById(R.id.text_quest_reward_4);
            mQuestContainer = (LinearLayout) itemView.findViewById(R.id.quest_container);

            /*for (TextView textView :
                    mType) {
                if (textView == null) {
                    continue;
                }

                textView.getBackground().setColorFilter(
                        ContextCompat.getColor(itemView.getContext(), R.color.colorAccent),
                        PorterDuff.Mode.SRC_ATOP);
            }*/
        }
    }

    public static class Expedition extends RecyclerView.ViewHolder {
        protected ExpandableLayout mExpandableLayout;
        protected TextView[] mRewardNumber;
        protected TextView[] mRequireNumber;
        protected TextView mTitle;
        protected TextView mTime;
        protected TextView mReward;
        protected TextView mFleetRequire;
        protected TextView mShipRequire;

        public Expedition(View itemView) {
            super(itemView);

            mExpandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandableLinearLayout);

            mTitle = (TextView) itemView.findViewById(android.R.id.title);
            mTime = (TextView) itemView.findViewById(R.id.text_time);

            mRewardNumber = new TextView[4];
            mRewardNumber[0] = (TextView) itemView.findViewById(R.id.text_number_0);
            mRewardNumber[1] = (TextView) itemView.findViewById(R.id.text_number_1);
            mRewardNumber[2] = (TextView) itemView.findViewById(R.id.text_number_2);
            mRewardNumber[3] = (TextView) itemView.findViewById(R.id.text_number_3);
            mReward = (TextView) itemView.findViewById(R.id.text_reward);
            mRequireNumber = new TextView[3];
            mRequireNumber[0] = (TextView) itemView.findViewById(R.id.text_number_4);
            mRequireNumber[1] = (TextView) itemView.findViewById(R.id.text_number_5);
            mRequireNumber[2] = (TextView) itemView.findViewById(R.id.text_number_6);

            mFleetRequire = (TextView) itemView.findViewById(R.id.text_fleet_require);
            mShipRequire = (TextView) itemView.findViewById(R.id.text_ship_require);
        }

        public void setRewardResource(String str, int i) {
            setRewardResource(Html.fromHtml(str), i);
        }

        public void setRewardResource(Spanned str, int i) {
            if (TextUtils.isEmpty(str)) {
                mRewardNumber[i].setText("0");
            } else {
                mRewardNumber[i].setText(str);
            }
        }

        public void setRequireResource(String str, int i) {
            if (TextUtils.isEmpty(str)) {
                mRequireNumber[i].setText("0");
            } else {
                mRequireNumber[i].setText(str);
            }
        }

        public void setRewardText(@Nullable String str1, @Nullable String str2) {
            StringBuilder sb = new StringBuilder();

            if (!TextUtils.isEmpty(str1)) {
                sb.append(str1);
            }

            if (!TextUtils.isEmpty(str2)) {
                if (sb.length() > 0) {
                    sb.append("<br>");
                }

                sb.append(str2);
            }

            setRewardNumber(Html.fromHtml(sb.toString()));
        }

        public void setRewardNumber(@Nullable Spanned str) {
            if (TextUtils.isEmpty(str)) {
                mReward.setVisibility(View.GONE);
            } else {
                mReward.setVisibility(View.VISIBLE);
                mReward.setText(str);
            }
        }

        @SuppressLint("DefaultLocale")
        public void setFleetRequire(int totalLevel, int flagshipLevel, int minShips) {
            StringBuilder sb = new StringBuilder();
            if (totalLevel != 0) {
                sb.append(String.format("舰队总等级 %d", totalLevel));
            }

            if (flagshipLevel != 0) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(String.format("旗舰等级 %d", flagshipLevel));
            }

            if (minShips != 0) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(String.format("最低舰娘数 %d", minShips));
            }

            mFleetRequire.setText(sb.toString());
        }

        public void setShipRequire(@Nullable String ship, @Nullable String bucket) {
            StringBuilder sb = new StringBuilder();
            if (!TextUtils.isEmpty(ship)) {
                sb.append(ship.replace(" ", "<br>"));
            }

            if (!TextUtils.isEmpty(bucket)) {
                if (sb.length() > 0) {
                    sb.append("<br>");
                }
                sb.append(bucket);
            }

            mShipRequire.setText(Html.fromHtml(sb.toString()));
        }
    }

    public static class ItemImprovement extends RecyclerView.ViewHolder {
        protected TextView mName;
        protected TextView mType;
        protected TextView mShip;
        protected ImageView mImageView;

        public ItemImprovement(View itemView) {
            super(itemView);

            //mCardView = (CardView) itemView.findViewById(R.id.cardView);
            mName = (TextView) itemView.findViewById(R.id.text_card_title);
            mType = (TextView) itemView.findViewById(R.id.text_card_item_improve_type);
            mShip = (TextView) itemView.findViewById(R.id.text_card_item_improve_ship);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public static class Item extends RecyclerView.ViewHolder {
        protected LinearLayout mLinearLayout;
        protected View mDivider;
        protected View mDummyView;
        protected View mDummyView2;
        protected TextView mName;
        protected TextView mTitle;
        protected ImageView mImageView;

        public Item(View itemView) {
            super(itemView);

            mDivider = itemView.findViewById(R.id.divider);
            mDummyView = itemView.findViewById(R.id.dummy_view);
            mDummyView2 = itemView.findViewById(R.id.dummy_view2);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            mTitle = (TextView) itemView.findViewById(android.R.id.title);
            mName = (TextView) itemView.findViewById(R.id.textView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public static class Ship extends RecyclerView.ViewHolder {
        protected LinearLayout mLinearLayout;
        protected View mDivider;
        protected View mDummyView;
        protected View mDummyView2;
        protected TextView mName;
        protected TextView mName2;
        protected TextView mTitle;

        public Ship(View itemView) {
            super(itemView);

            mDivider = itemView.findViewById(R.id.divider);
            mDummyView = itemView.findViewById(R.id.dummy_view);
            mDummyView2 = itemView.findViewById(R.id.dummy_view2);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            mTitle = (TextView) itemView.findViewById(android.R.id.title);
            mName = (TextView) itemView.findViewById(R.id.textView);
            mName2 = (TextView) itemView.findViewById(R.id.textView2);
        }
    }

    public static class Map extends RecyclerView.ViewHolder {
        //protected ExpandableLinearLayout mLinearLayout;
        protected ExpandableLayout mDetailContainer;
        protected TextView mTitle;
        protected TextView mTextView;
        protected ImageView mImageView;

        public Map(View itemView) {
            super(itemView);

            //mLinearLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLinearLayout);
            mDetailContainer = (ExpandableLayout) itemView.findViewById(R.id.expandableLinearLayout);
            mTitle = (TextView) itemView.findViewById(android.R.id.title);
            mTextView = (TextView) itemView.findViewById(R.id.textView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);

            /*mDetailContainer.post(new Runnable() {
                @Override
                public void run() {
                    //mDetailContainer.setExpanded(false);
                }
            });*/

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*mTitle.setTypeface(
                            mDetailContainer.getVisibility() == View.GONE ?
                                    Typeface.defaultFromStyle(Typeface.BOLD) :
                                    Typeface.defaultFromStyle(Typeface.NORMAL));*/

                    if (mDetailContainer.isExpanded()) {
                        mDetailContainer.setExpanded(false);
                        mTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                        //mDetailContainer.moveChild(0);
                    } else {
                        mDetailContainer.setExpanded(true);
                        mTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        //mDetailContainer.moveChild(1);
                    }

                    //mDetailContainer.toggle();
                    //mDetailContainer.setVisibility(
                    //       mDetailContainer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    public static class Message extends RecyclerView.ViewHolder {
        protected TextView mTitle;
        protected TextView mSummary;
        protected TextView mContent;
        protected LinearLayout mGalleryContainer;
        protected Button mPositiveButton;
        protected Button mNegativeButton;

        protected CountDownTimer mCountDownTimer;

        public Message(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(android.R.id.title);
            mSummary = (TextView) itemView.findViewById(android.R.id.summary);
            mContent = (TextView) itemView.findViewById(android.R.id.content);
            mGalleryContainer = (LinearLayout) itemView.findViewById(R.id.content_container);
            mPositiveButton = (Button) itemView.findViewById(android.R.id.button1);
            mNegativeButton = (Button) itemView.findViewById(android.R.id.button2);

            mContent.setSpannableFactory(MySpannableFactory.getInstance());
        }

        public void addImages(final List<String> urls) {
            mGalleryContainer.removeAllViews();
            mGalleryContainer.setVisibility(View.VISIBLE);

            int i = 0;
            for (String url : urls) {
                final int finalI = i;
                addImage(url, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageDisplayActivity.start(itemView.getContext(), urls, finalI, null, false);
                    }
                });
                i++;
            }
        }

        public void addImage(final String url, View.OnClickListener listener) {
            ImageView imageView = new ImageView(itemView.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Utils.dpToPx(80), Utils.dpToPx(80));
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            if (mGalleryContainer.getChildCount() > 0) {
                lp.leftMargin = Utils.dpToPx(8);
            }

            Glide.with(imageView.getContext())
                    .load(Utils.getGlideUrl(url))
                    .crossFade()
                    .into(imageView);

            mGalleryContainer.addView(imageView);

            imageView.setOnClickListener(listener);
        }

        @SuppressLint("DefaultLocale")
        protected String formatTimeLeft(long time) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%d 天", time / DateUtils.DAY_IN_MILLIS));
            time = time % DateUtils.DAY_IN_MILLIS;


            sb.append(String.format(" %d 小时", time / DateUtils.HOUR_IN_MILLIS));
            time = time % DateUtils.HOUR_IN_MILLIS;

            sb.append(String.format(" %d 分钟", time / DateUtils.MINUTE_IN_MILLIS));
            time = time % DateUtils.MINUTE_IN_MILLIS;

            sb.append(String.format(" %d 秒", time / DateUtils.SECOND_IN_MILLIS));

            return sb.toString();
        }

    }

    public static class MessageEquip extends RecyclerView.ViewHolder {
        protected LinearLayout mContainer;
        protected TextView mSummary;

        protected BusEventListener mBusEventListener;

        protected static class BusEventListener {
            public boolean isRegistered;
        }

        public MessageEquip(View itemView) {
            super(itemView);

            mSummary = (TextView) itemView.findViewById(android.R.id.summary);
            mContainer = (LinearLayout) itemView.findViewById(android.R.id.content);
        }

        public void setContent() {
            Context context = itemView.getContext();
            mContainer.removeAllViews();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+9:00"));
            int type = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            int count = 0;
            for (EquipImprovement item :
                    EquipImprovementList.get(context)) {
                if (count > 4) {
                    break;
                }

                boolean add = false;
                StringBuilder sb = new StringBuilder();
                for (EquipImprovement.SecretaryEntity entity : item.getSecretary()) {
                    if (entity.getDay().get(type)) {
                        add = true;
                        sb.append(sb.length() > 0 ? " / " : "");
                        sb.append(entity.getName());
                    }
                }

                if (add) {
                    item.setBookmarked(Settings.instance(context)
                            .getBoolean(String.format("equip_improve_%d", item.getId()), false));

                    if (item.isBookmarked()) {
                        final Equip equip = EquipList.findItemById(context, item.getId());
                        if (equip != null) {
                            count++;

                            View view = LayoutInflater.from(context).inflate(R.layout.item_message_equip, mContainer, false);
                            EquipTypeList.setIntoImageView((ImageView) view.findViewById(android.R.id.icon), item.getIcon());
                            ((TextView) view.findViewById(android.R.id.title)).setText(
                                    String.format("%s (%s)", equip.getName().get(context), sb.toString()));

                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), EquipDisplayActivity.class);
                                    intent.putExtra(EquipDisplayActivity.EXTRA_ITEM_ID, equip.getId());

                                    v.getContext().startActivity(intent);
                                }
                            });

                            mContainer.addView(view);
                        }
                    }
                }
            }

            if (count == 0) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_message_text, mContainer, false);
                ((TextView) view.findViewById(android.R.id.title)).setText(R.string.bookmarked_items_no);
                mContainer.addView(view);
            }

            View view = LayoutInflater.from(context).inflate(R.layout.item_message_more, mContainer, false);
            ((TextView) view.findViewById(android.R.id.title)).setText(R.string.all_equip_improve_item);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.instance().post(new ChangeNavigationDrawerItemAction(R.id.nav_item_improve));
                }
            });
            mContainer.addView(view);
        }
    }
}
