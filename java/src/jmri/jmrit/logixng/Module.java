package jmri.jmrit.logixng;

import java.util.Collection;

import jmri.NamedBean;
import jmri.jmrit.logixng.SymbolTable.InitialValueType;
import jmri.jmrit.logixng.SymbolTable.VariableData;

/**
 * Represent a LogixNG module.
 * A module is similar to a ConditionalNG, except that it can be used by
 * both ConditionalNGs and modules.
 *
 * @author Daniel Bergqvist Copyright (C) 2020
 */
public interface Module extends Base, NamedBean {
    
//    void setRootSocketType(FemaleSocketManager.SocketType socketType);
    
    FemaleSocketManager.SocketType getRootSocketType();
    
    FemaleSocket getRootSocket();
    
    void setCurrentConditionalNG(ConditionalNG conditionalNG);
    
    void addParameter(String name, boolean isInput, boolean isOutput);
    
    void addParameter(Parameter parameter);
    
//    public void removeParameter(String name);
    
    void addLocalVariable(
            String name,
            SymbolTable.InitialValueType initialValueType,
            String initialValueData);
    
//    public void removeLocalVariable(String name);
    
    Collection<Parameter> getParameters();
    
    Collection<VariableData> getLocalVariables();
    
    
    /**
     * The definition of a parameter.
     */
    interface Parameter {
        
        /**
         * The name of the parameter
         * @return the name
         */
        String getName();
        
        /**
         * Answer whenether or not the parameter is input to the module.
         * @return true if the parameter is input, false otherwise
         */
        boolean isInput();
        
        /**
         * Answer whenether or not the parameter is output to the module.
         * @return true if the parameter is output, false otherwise
         */
        boolean isOutput();
        
    }
    
    
    /**
     * Data for a parameter.
     */
    public static class ParameterData extends VariableData {
        
        public ReturnValueType _returnValueType = ReturnValueType.None;
        public String _returnValueData;
        
        public ParameterData(
                String name,
                InitialValueType initialValueType,
                String initialValueData,
                ReturnValueType returnValueType,
                String returnValueData) {
            
            super(name, initialValueType, initialValueData);
            
            _returnValueType = returnValueType;
            _returnValueData = returnValueData;
        }
        
        public ParameterData(ParameterData data) {
            super(data._name, data._initialValueType, data._initialValueData);
            _returnValueType = data._returnValueType;
            _returnValueData = data._returnValueData;
        }
        
        public ReturnValueType getReturnValueType() {
            return _returnValueType;
        }
        
        public String getReturnValueData() {
            return _returnValueData;
        }
        
    }
    
    
    /**
     * An enum that defines the types of initial value.
     */
    enum ReturnValueType {
        
        None(Bundle.getMessage("ReturnValueType_None")),
        LocalVariable(Bundle.getMessage("ReturnValueType_LocalVariable")),
        Memory(Bundle.getMessage("ReturnValueType_Memory"));
        
        private final String _descr;
        
        private ReturnValueType(String descr) {
            _descr = descr;
        }
        
        public String getDescr() {
            return _descr;
        }
    }
    
    
}
