package com.dz.app.wordez.event.handler;

import com.dz.app.wordez.dao.repository.UserQueryRecordRepository;
import com.dz.app.wordez.event.RecordUserQueryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class RecordUserQueryEventHandler {

    private final UserQueryRecordRepository userQueryRecordRepo;

    @EventListener
    @Async
    public void recordUserQuery(RecordUserQueryEvent event) throws ExecutionException, InterruptedException {
        userQueryRecordRepo.save(event.getUserQueryRecord()).toFuture().get();
    }
}
