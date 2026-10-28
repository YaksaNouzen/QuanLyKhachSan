package module.phong.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import module.phong.model.DuLieuPhong;
import module.phong.model.LoaiPhong;
import module.phong.model.Phong;
import module.phong.model.Tang;
import module.phong.service.PhongService;

public class PhongController {
    private final PhongService service;
    private DuLieuPhong data = DuLieuPhong.rong();
    private final List<Runnable> listeners = new ArrayList<>();
    private Consumer<String> errors = message -> {};
    private Consumer<Boolean> busyListener = value -> {};
    private boolean busy;
    private boolean refreshQueued;

    public PhongController(PhongService service) {
        this.service = service;
    }

    public void onError(Consumer<String> listener) {
        errors = listener;
    }

    public void onBusy(Consumer<Boolean> listener) {
        busyListener = listener;
    }

    public void onChange(Runnable listener) {
        listeners.add(listener);
    }

    public List<Phong> rooms() {
        return data.phongs();
    }

    public List<LoaiPhong> types() {
        return data.loais();
    }

    public List<Tang> floors() {
        return data.tangs();
    }

    public Phong room(String maPhong) {
        return rooms().stream().filter(p -> p.maPhong().equals(maPhong))
                .findFirst().orElse(null);
    }

    public LoaiPhong type(String maLoai) {
        return types().stream().filter(p -> p.maLoaiPhong().equals(maLoai))
                .findFirst().orElse(null);
    }

    public Tang floor(String maTang) {
        return floors().stream().filter(p -> p.maTang().equals(maTang))
                .findFirst().orElse(null);
    }

    public boolean isBusy() {
        return busy;
    }

    public void refresh() {
        if (busy) {
            refreshQueued = true;
            return;
        }
        run(() -> {}, () -> {});
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
        new SwingWorker<DuLieuPhong, Void>() {
            protected DuLieuPhong doInBackground() {
                action.run();
                return service.taiDuLieu();
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
                    if (refreshQueued) {
                        refreshQueued = false;
                        refresh();
                    }
                }
            }
        }.execute();
    }

    public void saveRoom(Phong phong, boolean sua, Runnable success) {
        run(() -> service.luuPhong(phong, sua), success);
    }

    public void saveType(LoaiPhong loai, boolean sua, Runnable success) {
        run(() -> service.luuLoai(loai, sua), success);
    }

    public void saveFloor(Tang tang, boolean sua, Runnable success) {
        run(() -> service.luuTang(tang, sua), success);
    }

    public void disableRoom(String maPhong, Runnable success) {
        run(() -> service.ngungSuDung(maPhong), success);
    }

    public void deleteType(String maLoai, Runnable success) {
        run(() -> service.xoaLoai(maLoai), success);
    }

    public void deleteFloor(String maTang, Runnable success) {
        run(() -> service.xoaTang(maTang), success);
    }
}
