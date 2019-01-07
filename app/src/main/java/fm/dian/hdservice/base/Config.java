package fm.dian.hdservice.base;

public final class Config {
    private String[] MediaServer;
    private String[] VideoServer;
    private String[] TransferServer;
    private String[] UserCenter;
    private Login login;
    private Player player;
    private Statistics statistics;
    private Update update;
    private Utils utils;

    public String[] getMediaServer() {
        return MediaServer;
    }

    public String[] getVideoServer() {
        return VideoServer;
    }

    public void setVideoServer(String[] videoServer) {
        VideoServer = videoServer;
    }

    public void setMediaServer(String[] mediaServer) {
        MediaServer = mediaServer;
    }

    public String[] getTransferServer() {
        return TransferServer;
    }

    public void setTransferServer(String[] transferServer) {
        TransferServer = transferServer;
    }

    public String[] getUserCenter() {
        return UserCenter;
    }

    public void setUserCenter(String[] userCenter) {
        UserCenter = userCenter;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public Utils getUtils() {
        return utils;
    }

    public void setUtils(Utils utils) {
        this.utils = utils;
    }

    public class Login {
        private String Login;
        private String NewLogin;
        private String ResetPassword;
        private String Signup;

        public String getLogin() {
            return Login;
        }

        public void setLogin(String login) {
            Login = login;
        }

        public String getNewLogin() {
            return NewLogin;
        }

        public void setNewLogin(String newLogin) {
            NewLogin = newLogin;
        }

        public String getResetPassword() {
            return ResetPassword;
        }

        public void setResetPassword(String resetPassword) {
            ResetPassword = resetPassword;
        }

        public String getSignup() {
            return Signup;
        }

        public void setSignup(String signup) {
            Signup = signup;
        }
    }

    public class Player {
        private String ModuleMaxLatency;
        private String ModulePlayLatency;

        public String getModuleMaxLatency() {
            return ModuleMaxLatency;
        }

        public void setModuleMaxLatency(String moduleMaxLatency) {
            ModuleMaxLatency = moduleMaxLatency;
        }

        public String getModulePlayLatency() {
            return ModulePlayLatency;
        }

        public void setModulePlayLatency(String modulePlayLatency) {
            ModulePlayLatency = modulePlayLatency;
        }
    }

    public class Statistics {
        private String BufferTimePostAddress;
        private String BufferTimePostInterval;
        private String BufferTimeRecordInterval;

        public String getBufferTimePostAddress() {
            return BufferTimePostAddress;
        }

        public void setBufferTimePostAddress(String bufferTimePostAddress) {
            BufferTimePostAddress = bufferTimePostAddress;
        }

        public String getBufferTimePostInterval() {
            return BufferTimePostInterval;
        }

        public void setBufferTimePostInterval(String bufferTimePostInterval) {
            BufferTimePostInterval = bufferTimePostInterval;
        }

        public String getBufferTimeRecordInterval() {
            return BufferTimeRecordInterval;
        }

        public void setBufferTimeRecordInterval(String bufferTimeRecordInterval) {
            BufferTimeRecordInterval = bufferTimeRecordInterval;
        }
    }

    public class Update {
        private boolean NeedUpdate;
        private boolean ForceUpdate;
        private String UpdateTitle;
        private String UpdateDescription;
        private String UpdateURL;

        public String getUpdateURL() {
            return UpdateURL;
        }

        public void setUpdateURL(String updateURL) {
            UpdateURL = updateURL;
        }

        public boolean isNeedUpdate() {
            return NeedUpdate;
        }

        public void setNeedUpdate(boolean needUpdate) {
            NeedUpdate = needUpdate;
        }

        public boolean isForceUpdate() {
            return ForceUpdate;
        }

        public void setForceUpdate(boolean forceUpdate) {
            ForceUpdate = forceUpdate;
        }

        public String getUpdateTitle() {
            return UpdateTitle;
        }

        public void setUpdateTitle(String updateTitle) {
            UpdateTitle = updateTitle;
        }

        public String getUpdateDescription() {
            return UpdateDescription;
        }

        public void setUpdateDescription(String updateDescription) {
            UpdateDescription = updateDescription;
        }
    }

    public class Utils {
        private String BugTracker;
        private String FeedBack;
        private String roomNamePrefix;
        private boolean NeedEarphone;
        private int LiveNumber;
        private boolean OpenBlackBoard;


        public String getBugTracker() {
            return BugTracker;
        }

        public void setBugTracker(String bugTracker) {
            BugTracker = bugTracker;
        }

        public String getFeedBack() {
            return FeedBack;
        }

        public void setFeedBack(String feedBack) {
            FeedBack = feedBack;
        }

        public String getRoomNamePrefix() {
            return roomNamePrefix;
        }

        public void setRoomNamePrefix(String roomNamePrefix) {
            this.roomNamePrefix = roomNamePrefix;
        }

        public boolean isNeedEarphone() {
            return NeedEarphone;
        }

        public void setNeedEarphone(boolean needEarphone) {
            NeedEarphone = needEarphone;
        }

        public boolean isOpenBlackBoard() {
            return OpenBlackBoard;
        }

        public void setOpenBlackBoard(boolean openBlackBoard) {
            OpenBlackBoard = openBlackBoard;
        }

        public int getLiveNumber() {
            return LiveNumber;
        }

        public void setLiveNumber(int liveNumber) {
            LiveNumber = liveNumber;
        }
    }

}
