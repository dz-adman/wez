package com.dz.app.wordez.event;

import com.dz.app.wordez.dao.entity.UserQueryRecord;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class RecordUserQueryEvent extends ApplicationEvent {
    private final UserQueryRecord userQueryRecord;
    public RecordUserQueryEvent(Object source, UserQueryRecord userQueryRecord) {
        super(source);
        this.userQueryRecord = userQueryRecord;
    }
}
