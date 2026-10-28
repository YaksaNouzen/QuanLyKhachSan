public class PhongBan {

    private int maPB;
    private String tenPB;

    public PhongBan() {
    }

    public PhongBan(int maPB, String tenPB) {
        this.maPB = maPB;
        this.tenPB = tenPB;
    }

    public int getMaPB() {
        return maPB;
    }

    public void setMaPB(int maPB) {
        this.maPB = maPB;
    }

    public String getTenPB() {
        return tenPB;
    }

    public void setTenPB(String tenPB) {
        this.tenPB = tenPB;
    }

    @Override
    public String toString() {
        return tenPB;
    }
}