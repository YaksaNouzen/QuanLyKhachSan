package Module_PhanHoi.model;

public class LichSuXuLyPhanHoi {
    private long maLichSu;
    public long getMaLichSu() { return maLichSu; }
    public void setMaLichSu(long value) { this.maLichSu = value; }
    private long maPhanHoi;
    public long getMaPhanHoi() { return maPhanHoi; }
    public void setMaPhanHoi(long value) { this.maPhanHoi = value; }
    private java.time.LocalDateTime thoiGianGhiNhan;
    public java.time.LocalDateTime getThoiGianGhiNhan() { return thoiGianGhiNhan; }
    public void setThoiGianGhiNhan(java.time.LocalDateTime value) { this.thoiGianGhiNhan = value; }
    private TrangThaiPhanHoi trangThaiTruoc;
    public TrangThaiPhanHoi getTrangThaiTruoc() { return trangThaiTruoc; }
    public void setTrangThaiTruoc(TrangThaiPhanHoi value) { this.trangThaiTruoc = value; }
    private TrangThaiPhanHoi trangThaiSau;
    public TrangThaiPhanHoi getTrangThaiSau() { return trangThaiSau; }
    public void setTrangThaiSau(TrangThaiPhanHoi value) { this.trangThaiSau = value; }
    private String noiDungXuLy;
    public String getNoiDungXuLy() { return noiDungXuLy; }
    public void setNoiDungXuLy(String value) { this.noiDungXuLy = value; }
    private String nguoiThucHien;
    public String getNguoiThucHien() { return nguoiThucHien; }
    public void setNguoiThucHien(String value) { this.nguoiThucHien = value; }
}
