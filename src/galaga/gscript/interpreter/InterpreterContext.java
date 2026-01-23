package galaga.gscript.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import galaga.gscript.types.values.Value;

public final class InterpreterContext {
    private final Interpreter interpreter;

    private final Map<String, Function<Map<String, Value>, Value>> nativeFunctions = new HashMap<>();

    private final Scope globalScope = new Scope(null);
    private Scope currentScope = globalScope;

    private boolean functionContext = false;
    private boolean loopContext = false;

    private boolean breakCurrentLoop = false;
    private boolean continueCurrentLoop = false;
    private Value returnValue = null;

    public InterpreterContext(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Interpreter getInterpreter() {
        return this.interpreter;
    }

    public Scope getGlobalScope() {
        return this.globalScope;
    }

    public Scope getScope() {
        return this.currentScope;
    }

    
    public Function<Map<String, Value>, Value> getNative(String name) {
        return this.nativeFunctions.get(name);
    }

    public void defineNative(String name, Function<Map<String, Value>, Value> function) {
        this.nativeFunctions.put(name, function);
    }


    public boolean isNativeDefined(String name) {
        return this.nativeFunctions.containsKey(name);
    }

    public <T> T function(Function<Void, T> runnable) {
        boolean previousInFunction = this.functionContext;
        this.functionContext = true;
        T value = runnable.apply(null);
        this.functionContext = previousInFunction;
        return value;
    }

    public void loop(Runnable runnable) {
        boolean previousInLoop = this.loopContext;
        this.loopContext = true;
        runnable.run();
        this.loopContext = previousInLoop;
    }

    public <T> T loop(Function<Void, T> runnable) {
        boolean previousInLoop = this.loopContext;
        this.loopContext = true;
        T value = runnable.apply(null);
        this.loopContext = previousInLoop;
        return value;
    }

    public void scope(Runnable runnable) {
        this.scope(this.currentScope, (v) -> {
            runnable.run();
            return null;
        });
    }

    public <T> T scope(Function<Void, T> runnable) {
        return this.scope(this.currentScope, runnable);
    }

    public <T> T scope(Scope parent, Function<Void, T> runnable) {
        Scope previousScope = this.currentScope;
        this.currentScope = new Scope(parent);
        T value = runnable.apply(null);
        this.currentScope = previousScope;
        return value;
    }

    public boolean isFunctionContext() {
        return this.functionContext;
    }

    public boolean isLoopContext() {
        return this.loopContext;
    }


    public boolean isLoopBreakRequested() {
        return this.breakCurrentLoop;
    }

    public void resetLoopBreak() {
        this.breakCurrentLoop = false;
    }

    public void requestLoopBreak() {
        this.breakCurrentLoop = true;
    }

    public boolean isLoopSkip() {
        return this.continueCurrentLoop;
    }

    public void resetLoopSkip() {
        this.continueCurrentLoop = false;
    }

    public void requestLoopSkip() {
        this.continueCurrentLoop = true;
    }

    
    public boolean hasFunctionReturn() {
        return this.returnValue != null;
    }

    public Value getFunctionReturn() {
        Value retVal = this.returnValue;
        this.returnValue = null;
        return retVal;
    }

    public void setFunctionReturn(Value value) {
        this.returnValue = value;
    }
}
