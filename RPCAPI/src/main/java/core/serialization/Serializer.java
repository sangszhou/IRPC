package core.serialization;

/**
 * Created by xinszhou on 23/03/2017.
 */
public interface Serializer {
    byte[] Serialize(Object object);
    <T> T Deserialize(byte[] bb);
}
