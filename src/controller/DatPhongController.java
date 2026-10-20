package controller;

import java.sql.Timestamp;
import model.DatPhong;
import service.DatPhongService;
import java.sql.SQLException;
import java.util.List;

public class DatPhongController {

    private final DatPhongService datPhongService;

    public DatPhongController() {
        this.datPhongService = new DatPhongService();
    }

    public List<DatPhong> layDanhSachDatPhong() throws SQLException {
        return datPhongService.layDanhSachDatPhong();
    }
    public void taoDatPhong(DatPhong dp) throws SQLException {
    datPhongService.taoDatPhong(dp);
}
    
    
public void capNhatLichDatPhong(
        DatPhong dp,
        Timestamp ngayNhanGoc,
        Timestamp ngayTraGoc) throws SQLException {

    datPhongService.capNhatLichDatPhong(
            dp,
            ngayNhanGoc,
            ngayTraGoc
    );
}


public void huyDatPhong(String maDatPhong) throws SQLException {
    datPhongService.huyDatPhong(maDatPhong);
}

public void xoaDatPhong(String maDatPhong) throws SQLException {
    datPhongService.xoaDatPhong(maDatPhong);
}

public void nhanPhong(String maDatPhong) throws SQLException {
    datPhongService.nhanPhong(maDatPhong);
}

public void traPhong(String maDatPhong) throws SQLException {
    datPhongService.traPhong(maDatPhong);
}

public List<String> timPhongTrong(
        Timestamp ngayNhanDuKien,
        Timestamp ngayTraDuKien) throws SQLException {

    if (ngayNhanDuKien == null || ngayTraDuKien == null) {
        throw new IllegalArgumentException(
                "Vui long nhap day du thoi gian nhan va tra phong.");
    }

    if (!ngayTraDuKien.after(ngayNhanDuKien)) {
        throw new IllegalArgumentException(
                "Ngay tra du kien phai sau ngay nhan du kien.");
    }

    return datPhongService.timPhongTrong(
            ngayNhanDuKien,
            ngayTraDuKien
    );
}


public void doiPhong(
        String maDatPhongCu,
        String maPhongMoi,
        String lyDo,
        String nguoiThucHien,
        String maDatPhongMoi,
        String maChuyenPhong) throws SQLException {

    datPhongService.doiPhong(
            maDatPhongCu,
            maPhongMoi,
            lyDo,
            nguoiThucHien,
            maDatPhongMoi,
            maChuyenPhong
    );
}


}