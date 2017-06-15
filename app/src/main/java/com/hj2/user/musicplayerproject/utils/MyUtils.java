package com.hj2.user.musicplayerproject.utils;

/**
 * Created by USER on 2017-05-16.
 */

public class MyUtils {

    public static class changePlayModeEvent {
        public changePlayModeEvent(String playMode) {
            this.playMode = playMode;
        }

        public String playMode;
    }


    public static class finishEvent {
        public finishEvent(int doFinish) {
            this.doFinish = doFinish;
        }

        public int doFinish;
    }

    public static class restartMainEvent {
        public restartMainEvent(int doRestart) {
            this.doRestart = doRestart;
        }

        public int doRestart;
    }


    public static class restartUpdateUiEvent {

        public restartUpdateUiEvent(boolean isPlaying) {
            this.isPlaying = isPlaying;
        }

        public boolean isPlaying;
    }


    public static class clearSelectedItemEvent {
        public clearSelectedItemEvent(boolean doClear) {
            this.doClear = doClear;
        }

        public boolean doClear;
    }

}
