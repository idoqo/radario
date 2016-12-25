package io.github.idoqo.radario.helpers;


import io.github.idoqo.radario.R;
import io.github.idoqo.radario.model.Notification;

public class NotificationHelper {
    Notification notification;

    public NotificationHelper(Notification notification){
        this.notification = notification;
    }

    /*
    returns the string that triggered the notification, could be the username or something else
     */
    public String getSubject(){
        String object;
        if (notification.getType() == Notification.TYPE_GRANTED_BADGE) {
            object = "";
        } else {
            object = notification.getData().getUsername();
        }
        return object;
    }

    /*
    returns the appropriate string that should join the username and the post/item depending on
    the notification type
     */
    public String getLink(){
        String link;
        switch (notification.getType()){
            case Notification.TYPE_MENTIONED:
                link = "mentioned you in";
                break;
            case Notification.TYPE_LIKED:
                link = "liked";
                break;
            case Notification.TYPE_REPLIED:
                link = "replied to";
                break;
            case Notification.TYPE_POSTED:
                link = "posted on";
                break;
            case Notification.TYPE_GRANTED_BADGE:
                link = "granted badge:";
                break;
            case Notification.TYPE_CUSTOM:
            default:
                link = "triggered your notifications";
                break;
        }
        return link;
    }

    /*
    returns the appropriate string that should server as the notification object i.ie if its a post,
    the post title.
     */
    public String getObject(){
        //todo account for private messages and group chats...
        String object;
        //if notification is a badge, stuffs like title would be null so just the badge name
        if (notification.getType() == Notification.TYPE_GRANTED_BADGE) {
            object = notification.getData().getBadgeName();
        } else {
            object = notification.getData().getTopicTitle();
        }
        return object;
    }

    /**
     * retrieves the integer value of the appropriate drawable based on the notification type
     * @return int drawable value of the right icon
     */
    public int getTypeIcon(){
        switch (notification.getType()) {
            case Notification.TYPE_LIKED:
                return R.drawable.ic_like;
            case Notification.TYPE_REPLIED:
                return R.drawable.ic_reply_white;
            case Notification.TYPE_POSTED:
                return R.drawable.ic_comment;
            case Notification.TYPE_QUOTED:
                return R.drawable.ic_quote;
            case Notification.TYPE_CUSTOM:
            default:
                return R.drawable.ic_notifications_white_48dp;
        }
    }
}
