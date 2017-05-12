package faizan.com.islamichandbook.model;

/**
 * Created by buste on 4/23/2017.
 */

public class Surah {

    private String surahId;
    private String surahAddress;
    private String SurahName;
    private String SuranFileName;

    public String getSuranFileName() {
        return SuranFileName;
    }

    public void setSuranFileName(String suranFileName) {
        SuranFileName = suranFileName;
    }



    public String getSurahName() {
        return SurahName;
    }

    public void setSurahName(String surahName) {
        SurahName = surahName;
    }



    public String getSurahId() {
        return surahId;
    }

    public void setSurahId(String surahId) {
        this.surahId = surahId;
    }

    public String getSurahAddress() {
        return surahAddress;
    }

    public void setSurahAddress(String surahAddress) {
        this.surahAddress = surahAddress;
    }


 }
