package com.jihun.study.openDartApi.entity.count;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class RequestCount implements Serializable {
    @EmbeddedId
    private RequestCountPK  requestCountPK;
    private int             count;

    public RequestCount() {
    }

    public RequestCount(RequestCountPK requestCountPK) {
        this(requestCountPK, 1);
    }

    public RequestCount(RequestCountPK requestCountPK, int count) {
        this.requestCountPK = requestCountPK;
        this.count = count;
    }

    public RequestCountPK getRequestCountPK() {
        return requestCountPK;
    }

    public void setRequestCountPK(RequestCountPK requestCountPK) {
        this.requestCountPK = requestCountPK;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
