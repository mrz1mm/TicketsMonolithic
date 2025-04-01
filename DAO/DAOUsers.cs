using System;
using System.ComponentModel.DataAnnotations;
using System.Security.Cryptography;

namespace myMonolithic.NET.DAO
{
    public class DAOUsers : BaseEntity
    {
        [Required]
        public string Username { get; set; }

        [Required]
        public string Email { get; set; }

        [Required]
        public string PasswordHash { get; set; }

        [Required]
        public string PasswordSalt { get; set; }


        // NULLABLE
        public DAOUserProfiles? UserProfile { get; set; }



        /// <summary>
        /// Genera una salt casuale, di default 16 byte.
        /// </summary>
        /// <param name="size">Dimensione in byte della salt</param>
        /// <returns>Salt in formato Base64</returns>
        public static string GenerateSalt(int size = 16)
        {
            byte[] saltBytes = new byte[size];
            using (var rng = RandomNumberGenerator.Create())
            {
                rng.GetBytes(saltBytes);
            }
            return Convert.ToBase64String(saltBytes);
        }

        /// <summary>
        /// Genera l’hash PBKDF2 (SHA256) della password concatenata con la salt.
        /// </summary>
        /// <param name="password">Password in chiaro inserita dall’utente</param>
        /// <param name="salt">Salt in Base64</param>
        /// <param name="iterations">Numero di iterazioni PBKDF2 (maggiore è più sicuro, ma più lento)</param>
        /// <param name="hashByteSize">Lunghezza in byte dell’hash finale</param>
        /// <returns>L’hash della password in Base64</returns>
        public static string HashPassword(string password, string salt, int iterations = 10000, int hashByteSize = 32)
        {
            // Converto salt da Base64 a byte[]
            byte[] saltBytes = Convert.FromBase64String(salt);

            // PBKDF2 con password + salt e un certo numero di iterazioni
            using (var pbkdf2 = new Rfc2898DeriveBytes(password, saltBytes, iterations, HashAlgorithmName.SHA256))
            {
                byte[] hash = pbkdf2.GetBytes(hashByteSize);  // Derivo i byte dell’hash
                return Convert.ToBase64String(hash);
            }
        }

        /// <summary>
        /// Verifica se la password tentata corrisponde a quella salvata (hash + salt).
        /// </summary>
        /// <param name="attemptedPassword">Password fornita dall’utente</param>
        /// <param name="storedSalt">Salt salvata (Base64)</param>
        /// <param name="storedHash">Hash salvato (Base64)</param>
        /// <returns>true se combaciano, false altrimenti</returns>
        public static bool VerifyPassword(string attemptedPassword, string storedSalt, string storedHash)
        {
            // Ricalcolo l’hash con la password tentata
            string attemptedHash = HashPassword(attemptedPassword, storedSalt);

            // Confronto costante (evita side-channel attacks)
            return SlowEquals(storedHash, attemptedHash);
        }

        /// <summary>
        /// Confronto "tempo costante" per evitare side-channel attacks.
        /// </summary>
        private static bool SlowEquals(string a, string b)
        {
            if (a == null || b == null) return false;
            if (a.Length != b.Length) return false;

            int diff = 0;
            for (int i = 0; i < a.Length; i++)
            {
                diff |= a[i] ^ b[i];
            }
            return diff == 0;
        }
    }
}
