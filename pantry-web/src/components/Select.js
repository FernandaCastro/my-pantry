import Select from 'react-select';

const customStyles = {
    singleValue: (provided, state) => ({
        ...provided,
        color: 'var(--text-color)',
      }),

    control: (provided, state) => ({
        ...provided,
        backgroundColor: 'var(--background)',
        borderColor: 'var(--border-color)',
        minHeight: '31px',
        height: '31px',
        boxShadow: null,
        fontSize: '15px',
        "&:hover": {
            borderColor: 'var(--link-color)'
        }
    }),

    valueContainer: (provided, state) => ({
        ...provided,
        height: '31px',
        padding: '0 6px',
    }),

    input: (provided, state) => ({
        ...provided,
        margin: '0px',
        color: 'var(--text-color)',
        borderColor: 'var(--border-color)',
        fontSize: '15px',
    }),

    placeholder: (provided, state) => ({
        ...provided,
        fontSize: '14px',
        color: 'var(--text-color)',
    }),

    indicatorSeparator: state => ({
        display: 'none',
    }),

    indicatorsContainer: (provided, state) => ({
        ...provided,
        height: '31px',
    }),

    menu: (provided, state) => ({
        ...provided,
        backgroundColor: 'var(--background)',
    }),

    option: (provided, { data, isDisabled, isFocused, isSelected }) => ({
        ...provided,
        backgroundColor: isSelected ? 'var(--highlight-item-list)' : 'var(--background)',
        color: 'var(--text-color)',
        minHeight: '30px',
        height: '30px',
        fontSize: '15px',
        "&:hover": {
            color: 'var(--highlight-text)'
        }
    }),
};

export default ({ name, placeholder, options, onChange, defaultValue, value, disabled }) =>
(<Select styles={customStyles}
    name={name}
    placeholder={placeholder}
    options={options}
    onChange={onChange}
    defaultValue={defaultValue}
    disabled={disabled}
    value={value}
/>
);

