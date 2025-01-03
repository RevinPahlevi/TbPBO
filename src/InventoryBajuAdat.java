import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

// Interface untuk operasi CRUD
// Kriteria: Interface dan implementasi dari interface
interface InventoryActions {
    void create(Connection conn, Scanner scanner); // Method untuk create data
    void read(Connection conn);                   // Method untuk read data
    void update(Connection conn, Scanner scanner); // Method untuk update data
    void delete(Connection conn, Scanner scanner); // Method untuk delete data
}

// Superclass untuk inventaris
// Kriteria: Memiliki superclass
class Inventory {
    protected String id;  // Atribut ID untuk inventaris
    protected String nama; // Atribut nama untuk inventaris

    // Konstruktor untuk Inventory
    // Kriteria: Terdiri dari konstruktor
    public Inventory(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    // Getter untuk ID
    public String getId() {
        return id;
    }

    // Getter untuk Nama
    public String getNama() {
        return nama;
    }
}

// Subclass khusus untuk Baju Adat
// Kriteria: Memiliki subclass yang mewarisi superclass
class BajuAdatInventory extends Inventory {
    private String ukuran; // Atribut tambahan untuk ukuran

    // Konstruktor untuk subclass
    public BajuAdatInventory(String id, String nama, String ukuran) {
        super(id, nama); // Memanggil konstruktor superclass
        this.ukuran = ukuran;
    }

    // Getter untuk ukuran
    public String getUkuran() {
        return ukuran;
    }
}

// Kelas utama dengan implementasi CRUD
// Kriteria: Menggunakan JDBC dan memiliki fungsi CRUD
public class InventoryBajuAdat implements InventoryActions {
    // Koneksi database
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/inventory_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "revinpahlevi";

    // Username dan password hardcoded untuk login
    private static final String HARDCODED_USERNAME = "revin";
    private static final String HARDCODED_PASSWORD = "pinbro14";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Percabangan: Login
        // Kriteria: Percabangan untuk login user
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Koneksi ke database berhasil.");

            while (!loginUser(scanner)) { // Perulangan untuk login
                System.out.println("Username atau password salah. Silakan coba lagi.\n");
            }

            InventoryBajuAdat app = new InventoryBajuAdat();
            boolean running = true;

            // Perulangan untuk menu utama
            // Kriteria: Terdapat perulangan
            while (running) {
                System.out.println("\nMenu:");
                System.out.println("1. Tambah Stok Baju Adat");
                System.out.println("2. Lihat Semua Stok Baju Adat");
                System.out.println("3. Update Stok Baju Adat");
                System.out.println("4. Hapus Stok Baju Adat");
                System.out.println("5. Hitung Jumlah Stok Baju Adat");
                System.out.println("6. Keluar");
                System.out.print("Pilih menu: ");

                if (!scanner.hasNextInt()) { // Percabangan untuk validasi input
                    System.out.println("Input tidak valid. Masukkan angka dari 1 hingga 6.");
                    scanner.nextLine(); // Clear invalid input
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                // Percabangan: Menu pilihan
                switch (choice) {
                    case 1:
                        app.create(conn, scanner); // CRUD: Create
                        break;
                    case 2:
                        app.read(conn); // CRUD: Read
                        break;
                    case 3:
                        app.update(conn, scanner); // CRUD: Update
                        break;
                    case 4:
                        app.delete(conn, scanner); // CRUD: Delete
                        break;
                    case 5:
                        app.count(conn); // Perhitungan: Menghitung jumlah stok
                        break;
                    case 6:
                        running = false;
                        System.out.println("Program selesai. Terima kasih!");
                        break;
                    default:
                        System.out.println("Pilihan tidak valid. Masukkan angka dari 1 hingga 6.");
                        break;
                }
            }
        } catch (SQLException e) { // Exception handling: SQLException
            System.out.println("Kesalahan koneksi ke database: " + e.getMessage());
        }
    }

    // Metode baru untuk menghitung jumlah stok
    // Kriteria: Perhitungan matematika menggunakan SQL COUNT
    public void count(Connection conn) {
        try {
            String sql = "SELECT COUNT(*) AS jumlah FROM baju_adat";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int jumlah = rs.getInt("jumlah");
                System.out.println("\nJumlah total stok baju adat: " + jumlah);
            }
        } catch (SQLException e) { // Exception handling
            System.out.println("Kesalahan saat menghitung jumlah stok baju adat: " + e.getMessage());
        }
    }

    @Override
    public void create(Connection conn, Scanner scanner) {
        try {
            System.out.print("Masukkan ID Baju Adat: ");
            String id = scanner.nextLine().trim(); // Manipulasi String: trim()
            System.out.print("Masukkan Nama Baju Adat: ");
            String nama = scanner.nextLine().trim(); // Manipulasi String: trim()
            System.out.print("Masukkan Ukuran: ");
            String ukuran = scanner.nextLine().trim(); // Manipulasi String: trim()

            if (id.isEmpty() || nama.isEmpty() || ukuran.isEmpty()) { // Validasi input kosong
                System.out.println("ID, nama, atau ukuran tidak boleh kosong.");
                return;
            }

            // Manipulasi Date
            java.util.Date currentDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime()); // Konversi ke SQL Date

            String sql = "INSERT INTO baju_adat (id, nama, ukuran, tanggal_input) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, nama);
            pstmt.setString(3, ukuran);
            pstmt.setDate(4, sqlDate);
            pstmt.executeUpdate();

            System.out.println("Stok baju adat berhasil ditambahkan.");
        } catch (SQLException e) { // Exception handling
            System.out.println("Kesalahan saat menambahkan stok baju adat: " + e.getMessage());
        }
    }

    @Override
    public void read(Connection conn) {
        try {
            String sql = "SELECT * FROM baju_adat";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Menggunakan Collection Framework: ArrayList
            ArrayList<BajuAdatInventory> inventoryList = new ArrayList<>();

            while (rs.next()) {
                BajuAdatInventory item = new BajuAdatInventory(
                    rs.getString("id"),
                    rs.getString("nama"),
                    rs.getString("ukuran")
                );
                inventoryList.add(item); // Menambahkan item ke ArrayList
            }

            // Menampilkan data dari ArrayList
            if (inventoryList.isEmpty()) {
                System.out.println("\nTidak ada data stok baju adat.");
            } else {
                System.out.println("\nData Stok Baju Adat:");
                for (BajuAdatInventory item : inventoryList) { // Perulangan untuk data
                    System.out.printf("ID: %s, Nama: %s, Ukuran: %s\n", 
                        item.getId(), item.getNama(), item.getUkuran());
                }
            }

        } catch (SQLException e) { // Exception handling
            System.out.println("Kesalahan saat membaca stok baju adat: " + e.getMessage());
        }
    }

    @Override
    public void update(Connection conn, Scanner scanner) {
        try {
            System.out.print("Masukkan ID Baju Adat yang ingin diupdate: ");
            String id = scanner.nextLine().trim(); // Manipulasi String: trim()
            System.out.print("Masukkan Nama Baru: ");
            String nama = scanner.nextLine().trim(); // Manipulasi String: trim()
            System.out.print("Masukkan Ukuran Baru: ");
            String ukuran = scanner.nextLine().trim(); // Manipulasi String: trim()

            String sql = "UPDATE baju_adat SET nama = ?, ukuran = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nama);
            pstmt.setString(2, ukuran);
            pstmt.setString(3, id);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Stok baju adat berhasil diupdate.");
            } else {
                System.out.println("ID tidak ditemukan.");
            }
        } catch (SQLException e) { // Exception handling
            System.out.println("Kesalahan saat mengupdate stok baju adat: " + e.getMessage());
        }
    }

    @Override
    public void delete(Connection conn, Scanner scanner) {
        try {
            System.out.print("Masukkan ID Baju Adat yang ingin dihapus: ");
            String id = scanner.nextLine().trim(); // Manipulasi String: trim()

            String sql = "DELETE FROM baju_adat WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Stok baju adat berhasil dihapus.");
            } else {
                System.out.println("ID tidak ditemukan.");
            }
        } catch (SQLException e) { // Exception handling
            System.out.println("Kesalahan saat menghapus stok baju adat: " + e.getMessage());
        }
    }

    private static boolean loginUser(Scanner scanner) {
        System.out.print("Masukkan Username: ");
        String username = scanner.nextLine().trim(); // Manipulasi String: trim()
        System.out.print("Masukkan Password: ");
        String password = scanner.nextLine().trim(); // Manipulasi String: trim()

        return username.equals(HARDCODED_USERNAME) && password.equals(HARDCODED_PASSWORD);
    }
}
