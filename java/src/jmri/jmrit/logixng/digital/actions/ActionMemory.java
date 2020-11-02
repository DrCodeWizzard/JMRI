package jmri.jmrit.logixng.digital.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import jmri.InstanceManager;
import jmri.JmriException;
import jmri.NamedBeanHandle;
import jmri.NamedBeanHandleManager;
import jmri.Memory;
import jmri.MemoryManager;
import jmri.jmrit.logixng.*;
import jmri.jmrit.logixng.util.parser.*;
import jmri.jmrit.logixng.util.parser.expressionnode.ExpressionNode;
import jmri.jmrit.logixng.util.parser.variables.LocalVariableExpressionVariable;
import jmri.util.ThreadingUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action sets the value of a memory.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class ActionMemory extends AbstractDigitalAction implements VetoableChangeListener {

    private NamedBeanHandle<Memory> _memoryHandle;
    private NamedBeanHandle<Memory> _otherMemoryHandle;
    private MemoryOperation _memoryOperation = MemoryOperation.SET_TO_STRING;
    private String _data = "";
    private ExpressionNode _expressionNode;
    
    public ActionMemory(String sys, String user)
            throws BadUserNameException, BadSystemNameException {
        super(sys, user);
    }
    
    public void setMemory(@Nonnull String memoryName) {
        assertListenersAreNotRegistered(log, "setMemory");
        Memory memory = InstanceManager.getDefault(MemoryManager.class).getMemory(memoryName);
        if (memory != null) {
            setMemory(memory);
        } else {
            removeMemory();
            log.error("memory \"{}\" is not found", memoryName);
        }
    }
    
    public void setMemory(@Nonnull NamedBeanHandle<Memory> handle) {
        assertListenersAreNotRegistered(log, "setMemory");
        _memoryHandle = handle;
        InstanceManager.memoryManagerInstance().addVetoableChangeListener(this);
    }
    
    public void setMemory(@Nonnull Memory memory) {
        assertListenersAreNotRegistered(log, "setMemory");
        setMemory(InstanceManager.getDefault(NamedBeanHandleManager.class)
                .getNamedBeanHandle(memory.getDisplayName(), memory));
    }
    
    public void removeMemory() {
        assertListenersAreNotRegistered(log, "setMemory");
        if (_memoryHandle != null) {
            InstanceManager.memoryManagerInstance().removeVetoableChangeListener(this);
            _memoryHandle = null;
        }
    }
    
    public NamedBeanHandle<Memory> getMemory() {
        return _memoryHandle;
    }
    
    public void setOtherMemory(String memoryName) {
        MemoryManager memoryManager = InstanceManager.getDefault(MemoryManager.class);
        Memory memory = memoryManager.getMemory(memoryName);
        if (memory != null) {
            _otherMemoryHandle = InstanceManager.getDefault(NamedBeanHandleManager.class).getNamedBeanHandle(memoryName, memory);
            memoryManager.addVetoableChangeListener(this);
        } else {
            _otherMemoryHandle = null;
            memoryManager.removeVetoableChangeListener(this);
        }
    }
    
    public void setOtherMemory(NamedBeanHandle<Memory> handle) {
        _otherMemoryHandle = handle;
        if (_otherMemoryHandle != null) {
            InstanceManager.getDefault(MemoryManager.class).addVetoableChangeListener(this);
        } else {
            InstanceManager.getDefault(MemoryManager.class).removeVetoableChangeListener(this);
        }
    }
    
    public void setOtherMemory(@CheckForNull Memory memory) {
        if (memory != null) {
            _otherMemoryHandle = InstanceManager.getDefault(NamedBeanHandleManager.class)
                    .getNamedBeanHandle(memory.getDisplayName(), memory);
            InstanceManager.getDefault(MemoryManager.class).addVetoableChangeListener(this);
        } else {
            _otherMemoryHandle = null;
            InstanceManager.getDefault(MemoryManager.class).removeVetoableChangeListener(this);
        }
    }
    
    public NamedBeanHandle<Memory> getOtherMemory() {
        return _otherMemoryHandle;
    }
    
    public void setMemoryOperation(MemoryOperation state) throws ParserException {
        _memoryOperation = state;
        parseFormula();
    }
    
    public MemoryOperation getMemoryOperation() {
        return _memoryOperation;
    }
    
    public void setData(String newValue) throws ParserException {
        _data = newValue;
        parseFormula();
    }
    
    public String getData() {
        return _data;
    }
    
    private void parseFormula() throws ParserException {
        if (_memoryOperation == MemoryOperation.FORMULA) {
            Map<String, Variable> variables = new HashMap<>();
            
            SymbolTable symbolTable =
                    InstanceManager.getDefault(LogixNG_Manager.class)
                            .getSymbolTable();
            
            if (symbolTable == null && 1==1) return;    // Why does this happens?
//            if (symbolTable == null && 1==1) return;    // Nothing we can do if we don't have a symbol table
            if (symbolTable == null) throw new RuntimeException("Daniel AA");
            if (symbolTable.getSymbols() == null) throw new RuntimeException("Daniel BB");
            if (symbolTable.getSymbols().values() == null) throw new RuntimeException("Daniel BB");
            
            for (SymbolTable.Symbol symbol : symbolTable.getSymbols().values()) {
                variables.put(symbol.getName(),
                        new LocalVariableExpressionVariable(symbol.getName()));
            }
            
            RecursiveDescentParser parser = new RecursiveDescentParser(variables);
            _expressionNode = parser.parseExpression(_data);
        } else {
            _expressionNode = null;
        }
    }
    
    @Override
    public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
        if ("CanDelete".equals(evt.getPropertyName())) { // No I18N
            if (evt.getOldValue() instanceof Memory) {
                if (evt.getOldValue().equals(getMemory().getBean())) {
                    PropertyChangeEvent e = new PropertyChangeEvent(this, "DoNotDelete", null, null);
                    throw new PropertyVetoException(Bundle.getMessage("Memory_MemoryInUseMemoryExpressionVeto", getDisplayName()), e); // NOI18N
                }
            }
        } else if ("DoDelete".equals(evt.getPropertyName())) { // No I18N
            if (evt.getOldValue() instanceof Memory) {
                if (evt.getOldValue().equals(getMemory().getBean())) {
                    removeMemory();
                }
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return Category.ITEM;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public void execute() throws JmriException {
        if (_memoryHandle == null) return;
        
        System.out.format("%n%nActionMemory.execute() %s%n", getSystemName());
        
        final Memory memory = _memoryHandle.getBean();
        
        AtomicReference<JmriException> ref = new AtomicReference<>();
        
        ThreadingUtil.runOnLayout(() -> {
            System.out.format("ActionMemory: oper: %s, memory: %s, memory value: %s, data: %s%n", _memoryOperation.name(), memory.getSystemName(), memory.getValue(), _data);
            
            switch (_memoryOperation) {
                case SET_TO_NULL:
                    memory.setValue(null);
                    break;
                    
                case SET_TO_STRING:
                    memory.setValue(_data);
                    break;
                    
                case COPY_VARIABLE_TO_MEMORY:
                    System.out.format("AXZ ActionMemory: SET_TO_VARIABLE: %s, %s%n", memory.getSystemName(), memory.getValue());
                    InstanceManager.getDefault(LogixNG_Manager.class)
                            .getSymbolTable().printSymbolTable(System.out);
                    
                    Object variableValue =
                            InstanceManager.getDefault(LogixNG_Manager.class)
                                    .getSymbolTable().getValue(_data);
                    System.out.format("AA ActionMemory: SET_TO_VARIABLE: %s, %s, %s%n", variableValue, memory.getSystemName(), memory.getValue());
                    variableValue = "Something else!!!";
//                    Object variableValue = "Something else!!!";
                    memory.setValue(variableValue);
                    
                    System.out.format("BB ActionMemory: SET_TO_VARIABLE: %s, %s, %s%n", variableValue, memory.getSystemName(), memory.getValue());
                    break;
                    
                case COPY_MEMORY_TO_MEMORY:
                    if (_otherMemoryHandle != null) {
                        memory.setValue(_otherMemoryHandle.getBean().getValue());
                    } else {
                        log.error("setMemory should copy memory to memory but other memory is null");
                    }
                    break;
                    
                case FORMULA:
                    if (_data.isEmpty()) {
                        memory.setValue(null);
                    } else {
                        try {
                            if (_expressionNode == null) {
                                System.out.format("ActionMemory: %s, _expressionNode is null%n", getSystemName());
                                return;
                            }
                            memory.setValue(_expressionNode.calculate());
                        } catch (JmriException e) {
                            ref.set(e);
                        }
                    }
                    break;
                    
                default:
                    throw new IllegalArgumentException("_memoryOperation has invalid value: {}" + _memoryOperation.name());
            }
        });
        
        if (ref.get() != null) throw ref.get();
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getShortDescription(Locale locale) {
        return Bundle.getMessage(locale, "Memory_Short");
    }

    @Override
    public String getLongDescription(Locale locale) {
        String memoryName;
        if (_memoryHandle != null) {
            memoryName = _memoryHandle.getBean().getDisplayName();
        } else {
            memoryName = Bundle.getMessage(locale, "BeanNotSelected");
        }
        
        String copyToMemoryName;
        if (_otherMemoryHandle != null) {
            copyToMemoryName = _otherMemoryHandle.getBean().getDisplayName();
        } else {
            copyToMemoryName = Bundle.getMessage(locale, "BeanNotSelected");
        }
        
        switch (_memoryOperation) {
            case SET_TO_NULL:
                return Bundle.getMessage(locale, "Memory_Long_Null", memoryName);
            case SET_TO_STRING:
                return Bundle.getMessage(locale, "Memory_Long_Value", memoryName, _data);
            case COPY_VARIABLE_TO_MEMORY:
                return Bundle.getMessage(locale, "Memory_Long_CopyVariableToMemory", _data, memoryName);
            case COPY_MEMORY_TO_MEMORY:
                return Bundle.getMessage(locale, "Memory_Long_CopyMemoryToMemory", copyToMemoryName, memoryName);
            case FORMULA:
                return Bundle.getMessage(locale, "Memory_Long_Formula", memoryName, _data);
            default:
                throw new IllegalArgumentException("_memoryOperation has invalid value: " + _memoryOperation.name());
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void setup() {
        // Do nothing
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
    }
    
    
    public enum MemoryOperation {
        SET_TO_NULL,
        SET_TO_STRING,
        COPY_VARIABLE_TO_MEMORY,
        COPY_MEMORY_TO_MEMORY,
        FORMULA;
    }
    
    private final static Logger log = LoggerFactory.getLogger(ActionMemory.class);
    
}
