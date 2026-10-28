package module.phong.model;

public enum TrangThaiPhong {
    TRONG("Trong"),
    DA_DAT("Da dat"),
    DANG_SU_DUNG("Dang su dung"),
    DANG_DON("Dang don"),
    BAO_TRI("Bao tri");

    private final String nhan;

    TrangThaiPhong(String nhan) {
        this.nhan = nhan;
    }

    public String toString() {
        return nhan;
    }
}
