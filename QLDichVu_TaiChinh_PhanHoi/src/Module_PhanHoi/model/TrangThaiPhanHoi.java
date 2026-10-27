package Module_PhanHoi.model;

public enum TrangThaiPhanHoi {
    MOI_TIEP_NHAN("Mới tiếp nhận"), DANG_XU_LY("Đang xử lý"), DA_XU_LY("Đã xử lý");
    private final String nhan;
    TrangThaiPhanHoi(String nhan) { this.nhan = nhan; }
    @Override public String toString() { return nhan; }
}
