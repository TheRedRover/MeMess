package domain;

import java.util.TimerTask;

public class TaskUserStatusUpdate extends TimerTask {

        private User user;

        public TaskUserStatusUpdate(User user){
            this.user=user;
        }

        @Override
        public void run() {
            user.setStatus(false);
        }

}
