package com.pentaware.doorish;

import com.pentaware.doorish.model.Offline_Requests;

public interface IOfflineRequests {

    void acceptEnquiry(Offline_Requests requests);

    void rejectEnquery(Offline_Requests requests);

}
