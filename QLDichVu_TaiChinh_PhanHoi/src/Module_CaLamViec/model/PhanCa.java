package Module_CaLamViec.model;

import java.sql.Date;

public class PhanCa {

    private int maPhanCa;
    private String maNV;
    private String maCa;
    private Date ngayLam;
    private String trangThai;
    private String noiDungBanGiao;

    // 3 thuộc tính dưới lấy từ JOIN, chỉ để hiển thị lên bảng
    private String tenNV;
    private String tenCa;
    private String gioLam;

    public PhanCa() {
    }

    public PhanCa(String maNV, String maCa, Date ngayLam) {
        this.maNV = maNV;
        this.maCa = maCa;
        this.ngayLam = ngayLam;
    }

    public int getMaPhanCa() {
        return maPhanCa;
    }

    public void setMaPhanCa(int maPhanCa) {
        this.maPhanCa = maPhanCa;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa;
    }

    public Date getNgayLam() {
        return ngayLam;
    }

    public void setNgayLam(Date ngayLam) {
        this.ngayLam = ngayLam;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getNoiDungBanGiao() {
        return noiDungBanGiao;
    }

    public void setNoiDungBanGiao(String noiDungBanGiao) {
        this.noiDungBanGiao = noiDungBanGiao;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public String getTenCa() {
        return tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }

    public String getGioLam() {
        return gioLam;
    }

    public void setGioLam(String gioLam) {
        this.gioLam = gioLam;
    }
}
