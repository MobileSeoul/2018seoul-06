package com.example.kccistc.parkingarea.list;

    /*
    설치목적구분: "생활방범",
    보관일수: "30",
    촬영방면정보: null,
    위도: "36.4835545",
    소재지지번주소: "충청북도 보은군 보은읍 삼산리 128-1",
    경도: "127.7160869",
    카메라화소수: "200",
    관리기관명: "충청북도 보은군청",
    소재지도로명주소: null,
    설치년월: null,
    카메라대수: "2",
    _id: 2,
    관리기관전화번호: "043-540-3126",
    데이터기준일자: "2018-02-19"
    */
public class CctvList {
    private String purpose;// 설치목적구분
    private String addr;// 소재지지번주소
    private String manageOffice;// 관리기관명
    private String countCamera;// 카메라대수
    private String setDate;// 설치년월
    private String tellNum;// 관리기관전화번호
    private String dataUpdate;// 데이터기준일자

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getManageOffice() {
            return manageOffice;
        }

        public void setManageOffice(String manageOffice) {
            this.manageOffice = manageOffice;
        }

        public String getCountCamera() {
            return countCamera;
        }

        public void setCountCamera(String countCamera) {
            this.countCamera = countCamera;
        }

        public String getSetDate() {
            return setDate;
        }

        public void setSetDate(String setDate) {
            this.setDate = setDate;
        }

        public String getTellNum() {
            return tellNum;
        }

        public void setTellNum(String tellNum) {
            this.tellNum = tellNum;
        }

        public String getDataUpdate() {
            return dataUpdate;
        }

        public void setDataUpdate(String dataUpdate) {
            this.dataUpdate = dataUpdate;
        }

        @Override
        public String toString() {
            return "CctvList{" +
                    "purpose='" + purpose + '\'' +
                    ", addr='" + addr + '\'' +
                    ", manageOffice='" + manageOffice + '\'' +
                    ", countCamera='" + countCamera + '\'' +
                    ", setDate=" + setDate +
                    ", tellNum='" + tellNum + '\'' +
                    ", dataUpdate=" + dataUpdate +
                    '}';
        }
    }
