package nam.gor.stocktaking.infrastucture.serializers;

public interface Serializer<T, D> {

    T fromDocument(D document);
    D toDocument(T domain);
}
