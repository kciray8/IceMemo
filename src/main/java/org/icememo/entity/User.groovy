package org.icememo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id

    String login

    String lastSeries = "-1";

    String lastSeason = "-1";

    Long lastLoginDate = 0;

    String lang;

    Boolean pauseOnSubtitles = false;

    public String getLang(){
        if(lang == null){
            lang = "rus"
        }
        return lang
    }

    Boolean getOffSR() {
        if(offSR == null){
            offSR = false;
        }

        return offSR
    }
    Boolean offSR;

    Long beginTime;
}
