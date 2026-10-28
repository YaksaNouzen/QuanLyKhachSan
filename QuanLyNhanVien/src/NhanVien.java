public class NhanVien {

    private String maNV;
    private String hoTen;
    private String phai;
    private int tuoi;
    private int maPB;

    public NhanVien() {
    }

    public NhanVien(
            String maNV,
            String hoTen,
            String phai,
            int tuoi,
            int maPB) {

        this.maNV = maNV;
        this.hoTen = hoTen;
        this.phai = phai;
        this.tuoi = tuoi;
        this.maPB = maPB;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getPhai() {
        return phai;
    }

    public void setPhai(String phai) {
        this.phai = phai;
    }

    public int getTuoi() {
        return tuoi;
    }

    public void setTuoi(int tuoi) {
        this.tuoi = tuoi;
    }

    public int getMaPB() {
        return maPB;
    }

    public void setMaPB(int maPB) {
        this.maPB = maPB;
    }

    @Override
    public String toString() {
        return hoTen;
    }
}