# Database patches

Mỗi thay đổi sau baseline đặt trong một file riêng, theo thứ tự:

```text
001_<muc-dich>.sql
002_<muc-dich>.sql
```

Không sửa lại patch đã được chia sẻ. Mỗi patch cần ghi database đích, điều kiện
chạy, đối tượng bị ảnh hưởng, cách kiểm tra và phương án phục hồi.

