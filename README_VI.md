# AutoSell Fabric — Minecraft 1.21.1

Mod client-side cho Fabric, thiết kế để chạy trên Minecraft Java ở PC và PojavLauncher Android.

## Tính năng hiện có
- Mở giao diện bằng `Right Shift`.
- Bật/tắt AutoSell.
- Bật/tắt chế độ lệnh và chế độ GUI.
- Chọn vật phẩm từ túi đồ để đưa vào danh sách bán.
- Lưu cấu hình tại `config/autosell.json`.
- Có khoảng nghỉ tối thiểu 5 giây giữa các thao tác tự động.

## Quan trọng trước khi dùng
Đây là project mẫu cho **Minecraft 1.21.1**, không phải mọi bản 1.21.x. Fabric mod thường cần build riêng cho từng nhánh Minecraft.
Chỉ bật AutoSell khi server cho phép tự động hóa. Một số server cấm macro/auto-clicker.

### Chế độ lệnh
Mở `config/autosell.json`, sửa:
```json
"commandMode": true,
"sellCommand": "/sell {item}",
"intervalSeconds": 10
```
`{item}` được thay bằng ID vật phẩm, ví dụ `minecraft:cobblestone`; `{count}` được thay bằng số lượng trong túi.
Mỗi server có cú pháp riêng. Nếu server chỉ hỗ trợ `/sell hand`, hãy đổi `sellCommand` thành `/sell hand` (lưu ý: lựa chọn vật phẩm khi đó chỉ dùng để kiểm tra túi, lệnh server có thể bán vật phẩm đang cầm thay vì ID được chọn).

### Chế độ GUI
Mặc định `guiMode` là `false`. Chỉ bật sau khi bạn biết rõ giao diện shop của server:
```json
"guiMode": true,
"guiTitleContains": "Sell",
"guiSellSlot": 13
```
`guiSellSlot` là chỉ số slot bắt đầu từ 0 trong container, không phải tọa độ pixel. Giao diện shop khác nhau giữa server nên cần kiểm tra chính xác. Khi bật, mod chỉ nhấn slot đã cấu hình khi tiêu đề cửa sổ khớp.

## Build thành file JAR
Yêu cầu JDK 21 và Gradle 8.10.x (hoặc Gradle tương thích với Fabric Loom 1.9.2).

Trong thư mục project:
```bash
gradle build
```
File JAR sẽ nằm trong:
`build/libs/autosell-fabric-1.0.0.jar`

## Cài trên PC
1. Cài Fabric Loader cho Minecraft 1.21.1.
2. Cài Fabric API đúng bản 1.21.1.
3. Chép `autosell-fabric-1.0.0.jar` vào thư mục `mods`.
4. Khởi động profile Fabric.

## Cài trên Android / PojavLauncher
1. Tạo profile Minecraft Java 1.21.1 với Fabric Loader.
2. Cài Fabric API 1.21.1 vào cùng thư mục `mods`.
3. Chép file JAR AutoSell vào `mods` của đúng instance.
4. Khởi động game. Mở giao diện bằng Right Shift (có thể cần gán phím tương đương trong cài đặt điều khiển Pojav).

## Giới hạn
- Project này nhắm tới 1.21.1; 1.21.4/1.21.5/1.21.8... có thể cần mappings/API và build riêng.
- Chế độ lệnh phụ thuộc plugin kinh tế của server.
- Chế độ GUI phụ thuộc tiêu đề và slot shop của từng server.
- Không có cách bán phổ quát hoạt động trên mọi SMP.
- Đây là mã nguồn project; chưa phải JAR đã build và kiểm thử trong Minecraft.
