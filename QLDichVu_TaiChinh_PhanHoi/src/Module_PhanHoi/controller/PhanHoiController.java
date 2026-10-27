package Module_PhanHoi.controller;

import Module_PhanHoi.model.*;
import Module_PhanHoi.service.PhanHoiService;
import java.time.LocalDateTime;
import java.util.List;

public class PhanHoiController {
    private final PhanHoiService service;
    public PhanHoiController() { this(new PhanHoiService()); }
    public PhanHoiController(PhanHoiService service) { this.service = service; }
    public List<PhanHoi> search(String keyword, LoaiPhanHoi type, TrangThaiPhanHoi state) {
        return service.search(keyword, type, state);
    }
    public PhanHoi findById(long id) { return service.findById(id); }
    public List<LichSuXuLyPhanHoi> history(long id) { return service.history(id); }
    public long tiepNhan(String customer, LoaiPhanHoi type, String content, LocalDateTime time, String actor) {
        return service.tiepNhan(customer, type, content, time, actor);
    }
    public void capNhatXuLy(long id, byte[] version, TrangThaiPhanHoi target, String content, String actor) {
        service.capNhatXuLy(id, version, target, content, actor);
    }
}
