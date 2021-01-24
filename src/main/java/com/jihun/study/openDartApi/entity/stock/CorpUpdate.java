package com.jihun.study.openDartApi.entity.stock;

import com.jihun.study.openDartApi.dto.DartDto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class CorpUpdate implements Serializable, DartDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int     id;
    private boolean isSuccess;
    private Date    updateDate;

    public CorpUpdate() {
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
