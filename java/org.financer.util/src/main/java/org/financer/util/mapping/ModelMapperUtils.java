package org.financer.util.mapping;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelMapperUtils {

    private static ModelMapper modelMapper;

    /*
      Model mapper property setting are specified in the following block.
      Default property matching strategy is set to Strict see {@link MatchingStrategies}
      Custom mappings are added using {@link ModelMapper#addMappings(PropertyMap)}
     */
    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public static void setModelMapper(ModelMapper modelMapper) {
        ModelMapperUtils.modelMapper = modelMapper;
    }

    /**
     * Hide from public usage.
     */
    private ModelMapperUtils() {
    }

    /**
     * <p>Note: outClass object must have default constructor with no arguments</p>
     *
     * @param <D>      type of result object.
     * @param <T>      type of source object to map from.
     * @param entity   entity that needs to be mapped.
     * @param outClass class of result object.
     * @return new object of <code>outClass</code> type.
     */
    public static <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    /**
     * <p>Note: outClass object must have default constructor with no arguments</p>
     *
     * @param entityList list of entities that needs to be mapped
     * @param outCLass   class of result list element
     * @param <D>        type of objects in result list
     * @param <T>        type of entity in <code>entityList</code>
     * @return list of mapped object with <code><D></code> type.
     */
    public static <D, T> List<D> mapAll(final List<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }

    /**
     * <p>Note: outClass object must have default constructor with no arguments</p>
     *
     * @param entityList list of entities that needs to be mapped
     * @param outCLass   class of result list element
     * @param <D>        type of objects in result list
     * @param <T>        type of entity in <code>entityList</code>
     * @return list of mapped object with <code><D></code> type.
     */
    public static <D, T> Set<D> mapAll(final Set<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toSet());
    }

    public static <K, V, K1, V1> Map<K, V> map(final Map<K1, V1> map, Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> result = new HashMap<>();
        map.forEach((key, value) ->
                result.put(modelMapper.map(key, keyClass), modelMapper.map(value, valueClass)));
        return result;
    }

    /**
     * Maps {@code source} to {@code destination}.
     *
     * @param source      object to map from
     * @param destination object to map to
     */
    public static <S, D> D map(final S source, D destination) {
        modelMapper.map(source, destination);
        return destination;
    }
}
