package Module_BuongPhong.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import Module_BuongPhong.model.CongViecVeSinh;
import Module_BuongPhong.service.BuongPhongService;

public class BuongPhongController {
    private final BuongPhongService service;
    private List<CongViecVeSinh> data = List.of();
    private final List<Runnable> listeners = new ArrayList<>();
    private Consumer<String> errors = message -> {};
    private Consumer<Boolean> busyListener = value -> {};
    private boolean busy;

    public BuongPhongController(BuongPhongService service) {
        this.service = service;
    }

    public List<CongViecVeSinh> congViecs() {
        return data;
    }

    public void onChange(Runnable listener) {
        listeners.add(listener);
    }

    public void onError(Consumer<String> listener) {
        errors = listener;
    }

    public void onBusy(Consumer<Boolean> listener) {
        busyListener = listener;
    }

    public void refresh() {
        run(() -> {}, () -> {});
    }

    public void phanCong(String maPhong, String noiDung, String nguoiPhuTrach) {
        run(() -> service.phanCong(maPhong, noiDung, nguoiPhuTrach), () -> {});
    }

    public void batDauDon(String maVeSinh) {
        run(() -> service.batDauDon(maVeSinh), () -> {});
    }

    public void xacNhanHoanThanh(String maVeSinh, String nguoiXacNhan) {
        run(() -> service.xacNhanHoanThanh(maVeSinh, nguoiXacNhan), () -> {});
    }

    public void huyPhanCong(String maVeSinh) {
        run(() -> service.huyPhanCong(maVeSinh), () -> {});
    }

    private void run(Runnable action, Runnable success) {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Controller phai duoc goi tren EDT.");
        if (busy) {
            errors.accept("Dang xu ly, vui long cho.");
            return;
        }
        busy = true;
        busyListener.accept(true);
        new SwingWorker<List<CongViecVeSinh>, Void>() {
            protected List<CongViecVeSinh> doInBackground() {
                action.run();
                return service.danhSach();
            }

            protected void done() {
                try {
                    data = get();
                    listeners.forEach(Runnable::run);
                    success.run();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    errors.accept(cause.getMessage());
                } finally {
                    busy = false;
                    busyListener.accept(false);
                }
            }
        }.execute();
    }
}
