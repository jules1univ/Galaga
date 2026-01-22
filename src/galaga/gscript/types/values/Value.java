package galaga.gscript.types.values;

public sealed interface Value permits NullValue, BooleanValue, IntegerValue, FloatValue, StringValue , ListValue, MapValue, FunctionValue {
}
