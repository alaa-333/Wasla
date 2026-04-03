package com.example.wasla.notification.event;


import com.example.wasla.notification.entity.WaslaEventType;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;


@Getter
public class WaslaNotificationEvent extends ApplicationEvent {

    private final WaslaEventType         eventType;
    private final UUID                   recipientUserId;
    private final RecipientRole          recipientRole;
    private final String                 title;
    private final String                 body;
    private final Map<String, String>    data;
    private final UUID                   jobId;
    private final UUID                   bidId;
    private final UUID                   actorUserId;
    private final String                 priority;
    private final long                   ttlSeconds;
    private final boolean                dataOnly;

    public enum RecipientRole { CUSTOMER, DRIVER }

    // Private constructor — use the Builder
    private WaslaNotificationEvent(Object source, Builder b) {
        super(source);
        this.eventType       = b.eventType;
        this.recipientUserId = b.recipientUserId;
        this.recipientRole   = b.recipientRole;
        this.title           = b.title;
        this.body            = b.body;
        this.data            = b.data != null ? b.data : Map.of();
        this.jobId           = b.jobId;
        this.bidId           = b.bidId;
        this.actorUserId     = b.actorUserId;
        this.priority        = b.priority != null ? b.priority : "NORMAL";
        this.ttlSeconds      = b.ttlSeconds > 0 ? b.ttlSeconds : 3600;
        this.dataOnly        = b.dataOnly;
    }

    public static Builder builder(Object source) {
        return new Builder(source);
    }

    public static class Builder {
        private final Object source;
        private WaslaEventType      eventType;
        private UUID                recipientUserId;
        private RecipientRole       recipientRole;
        private String              title;
        private String              body;
        private Map<String, String> data;
        private UUID                jobId;
        private UUID                bidId;
        private UUID                actorUserId;
        private String              priority;
        private long                ttlSeconds;
        private boolean             dataOnly;

        Builder(Object source) { this.source = source; }

        public Builder eventType(WaslaEventType v)       { this.eventType = v;       return this; }
        public Builder recipientUserId(UUID v)           { this.recipientUserId = v; return this; }
        public Builder recipientRole(RecipientRole v)    { this.recipientRole = v;   return this; }
        public Builder title(String v)                   { this.title = v;           return this; }
        public Builder body(String v)                    { this.body = v;            return this; }
        public Builder data(Map<String, String> v)       { this.data = v;            return this; }
        public Builder jobId(UUID v)                     { this.jobId = v;           return this; }
        public Builder bidId(UUID v)                     { this.bidId = v;           return this; }
        public Builder actorUserId(UUID v)               { this.actorUserId = v;     return this; }
        public Builder priority(String v)                { this.priority = v;        return this; }
        public Builder ttlSeconds(long v)                { this.ttlSeconds = v;      return this; }
        public Builder dataOnly(boolean v)               { this.dataOnly = v;        return this; }

        public WaslaNotificationEvent build() {
            return new WaslaNotificationEvent(source, this);
        }
    }
}
