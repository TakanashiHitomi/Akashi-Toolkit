package rikka.akashitoolkit.otto;

/**
 * Created by Rikka on 2016/4/1.
 */
public class ShipAction {
    public static class ShowFinalVersionChangeAction {
        private boolean showFinalVersion;

        public ShowFinalVersionChangeAction(boolean showFinalVersion) {
            this.showFinalVersion = showFinalVersion;
        }

        public boolean isShowFinalVersion() {
            return showFinalVersion;
        }

        public void setShowFinalVersion(boolean showFinalVersion) {
            this.showFinalVersion = showFinalVersion;
        }
    }

    public static class TypeChangeAction {
        private int type;

        public TypeChangeAction(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class SpeedChangeAction {
        private int speed;

        public SpeedChangeAction(int type) {
            this.speed = type;
        }

        public int getType() {
            return speed;
        }

        public void setType(int speed) {
            this.speed = speed;
        }
    }
}