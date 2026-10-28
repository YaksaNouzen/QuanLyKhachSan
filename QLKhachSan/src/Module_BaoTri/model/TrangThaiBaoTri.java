package Module_BaoTri.model;

public enum TrangThaiBaoTri {
    BAO_HONG("Bao hong"),
    DANG_SUA("Dang sua"),
    HOAN_TAT("Hoan tat"),
    HUY("Da huy");

    private final String nhan;

    TrangThaiBaoTri(String nhan) {
        this.nhan = nhan;
    }

    public String toString() {
        return nhan;
    }
}
