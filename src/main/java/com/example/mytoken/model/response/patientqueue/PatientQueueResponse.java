package com.example.mytoken.model.response.patientqueue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PatientQueueResponse {

    Long waiting;
    Long process;
    Long hold;
    Long completed;
    Long cancelled;
    Long waitingTime; // In min
    List<PatientDetailsResponse> list;
}
