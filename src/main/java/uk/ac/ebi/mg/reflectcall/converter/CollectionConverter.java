package uk.ac.ebi.mg.reflectcall.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uk.ac.ebi.mg.reflectcall.ConverterFactory;
import uk.ac.ebi.mg.reflectcall.exception.ConvertionException;

public class CollectionConverter extends ArrayConverter {

    public CollectionConverter(ConverterFactory convFact) {
        super(convFact);
    }

    @Override
    public Object convert(String val, Type targetType) throws ConvertionException {
        Class<?> elClass = null;
        Type elType = null;

        if (targetType instanceof ParameterizedType) {
            elType = ((ParameterizedType) targetType).getActualTypeArguments()[0];

            if (elType instanceof Class) {
                elClass = (Class<?>) elType;
            } else if (elType instanceof ParameterizedType) {
                elClass = (Class<?>) ((ParameterizedType) elType).getRawType();
            } else if (elType instanceof WildcardType) {
                elType = ((WildcardType) elType).getUpperBounds()[0];
                elClass = (Class<?>) elType;
            } else {
                throw new ConvertionException("Can't instantiate elements for type: " + targetType);
            }
        } else if (targetType instanceof Class) {
            elClass = (Class<?>) targetType;
            elType = targetType;
        } else {
            throw new ConvertionException("Can't instantiate elements for type: " + targetType);
        }

        List<String> parts = splitString(val);

        Collection<Object> outcoll = new ArrayList<Object>();

        for (String s : parts) {
            outcoll.add(getFactory().getConverter(elType, s).convert(s, elType));
        }

        return outcoll;
    }


}
