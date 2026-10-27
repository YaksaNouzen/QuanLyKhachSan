package Module_PhanHoi.model;

public enum LoaiPhanHoi {
    DANH_GIA("Đánh giá"), KHIEU_NAI("Khiếu nại");
    private final String nhan;
    LoaiPhanHoi(String nhan) { this.nhan = nhan; }
    @Override public String toString() { return nhan; }
}
