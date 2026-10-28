package model;

import java.sql.Time;

public class CaLam {

    private String maCa;
    private String tenCa;
    private String gioBatDau;   // dạng "HH:mm", ví dụ "06:00"
    private String gioKetThuc;

    public CaLam() {
    }

    public CaLam(String maCa, String tenCa, String gioBatDau, String gioKetThuc) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
    }

    // Time 06:00:00 (đọc từ SQL) -> "06:00"
    public static String catGio(Time gio) {
        return gio.toString().substring(0, 5);
    }

    // Đổi "HH:mm" ra số phút tính từ 0h. VD: "06:30" -> 390
    public static int doiRaPhut(String gio) {
        String[] tach = gio.split(":");
        return Integer.parseInt(tach[0]) * 60 + Integer.parseInt(tach[1]);
    }

    public int getPhutBatDau() {
        return doiRaPhut(gioBatDau);
    }

    // Ca qua đêm (VD 22:00 -> 06:00) thì cộng thêm 24 tiếng cho giờ kết thúc
    public int getPhutKetThuc() {
        int ketThuc = doiRaPhut(gioKetThuc);
        if (ketThuc <= getPhutBatDau()) {
            ketThuc = ketThuc + 24 * 60;
        }
        return ketThuc;
    }

    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa;
    }

    public String getTenCa() {
        return tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }

    public String getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(String gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public String getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(String gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    // Hiển thị trong JComboBox
    @Override
    public String toString() {
        return maCa + " - " + tenCa + " (" + gioBatDau + " - " + gioKetThuc + ")";
    }
}
