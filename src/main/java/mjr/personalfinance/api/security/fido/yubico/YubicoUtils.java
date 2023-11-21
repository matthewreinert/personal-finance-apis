package mjr.personalfinance.api.security.fido.yubico;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.yubico.webauthn.data.ByteArray;

public class YubicoUtils {

    private YubicoUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static ByteArray toByteArray(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return new ByteArray(buffer.array());
    }

    public static UUID toUUID(ByteArray byteArray) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray.getBytes());
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }
}
