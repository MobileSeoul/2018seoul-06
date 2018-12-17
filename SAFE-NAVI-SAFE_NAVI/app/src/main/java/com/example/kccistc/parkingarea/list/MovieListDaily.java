package com.example.kccistc.parkingarea.list;

public class MovieListDaily {
//    rnum: "1",
//    rank: "1",
//    rankInten: "0",
//    rankOldAndNew: "OLD",
//    movieCd: "20112207",
//    movieNm: "미션임파서블:고스트프로토콜",
//    openDt: "2011-12-15",
//    salesAmt: "2776060500",
//    salesShare: "36.3",
//    salesInten: "-415699000",
//    salesChange: "-13",
//    salesAcc: "40541108500",
//    audiCnt: "353274",
//    audiInten: "-60106",
//    audiChange: "-14.5",
//    audiAcc: "5328435",
//    scrnCnt: "697",
//    showCnt: "3223",
    private String rank;    //순위
    private String MovieNm; //영화 이름
    private String openDt; //상영 날짜
    private String audiAcc; //누적 관객수

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getMovieNm() {
        return MovieNm;
    }

    public void setMovieNm(String movieNm) {
        MovieNm = movieNm;
    }

    public String getOpenDt() {
        return openDt;
    }

    public void setOpenDt(String openDt) {
        this.openDt = openDt;
    }

    public String getAudiAcc() {
        return audiAcc;
    }

    public void setAudiAcc(String audiAcc) {
        this.audiAcc = audiAcc;
    }

    @Override
    public String toString() {
        return rank + ". " + MovieNm + "(" + openDt + ")" + ", 누적관객수(" + audiAcc + ")";
    }
}
