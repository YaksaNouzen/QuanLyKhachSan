package Module_BaoTri.model;

public enum MucDoUuTien {
    THAP("Thap"),
    TRUNG_BINH("Trung binh"),
    CAO("Cao");

    private final String nhan;

    MucDoUuTien(String nhan) {
        this.nhan = nhan;
    }

    public String toString() {
        return nhan;
    }
}
