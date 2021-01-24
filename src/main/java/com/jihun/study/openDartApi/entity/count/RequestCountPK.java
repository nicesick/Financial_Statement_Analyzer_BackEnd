package com.jihun.study.openDartApi.entity.count;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
public class RequestCountPK implements Serializable {
    private LocalDate localDate;
    private LocalTime localTime;

    public RequestCountPK() {
    }

    public RequestCountPK(LocalDate localDate, LocalTime localTime) {
        this.localDate = localDate;
        this.localTime = localTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestCountPK that = (RequestCountPK) o;

        if (!localDate.equals(that.localDate)) return false;
        return localTime.equals(that.localTime);
    }

    @Override
    public int hashCode() {
        int result = localDate.hashCode();
        result = 31 * result + localTime.hashCode();
        return result;
    }
}
