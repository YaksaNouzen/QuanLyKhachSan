package module.buongphong.model;

public enum TrangThaiVeSinh {
    CHO_DON("Cho don"),
    DANG_DON("Dang don"),
    HOAN_THANH("Hoan thanh"),
    HUY("Da huy");

    private final String nhan;

    TrangThaiVeSinh(String nhan) {
        this.nhan = nhan;
    }

    public String toString() {
        return nhan;
    }
}
