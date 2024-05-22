import Select from 'react-select';
import '../assets/styles/App.scss';

const customStyles = {

    control: (provided, state) => ({
        ...provided,
        backgroundColor: '#fff',
        borderColor: 'lightgray',
        minHeight: '31px',
        height: '31px',
        boxShadow: null,
        "&:hover": {
            borderColor: 'rgb(63, 6, 250)'
        }
    }),

    valueContainer: (provided, state) => ({
        ...provided,
        height: '31px',
        padding: '0 6px'
    }),

    input: (provided, state) => ({
        ...provided,
        margin: '0px',
        borderColor: '#fff',
    }),

    indicatorSeparator: state => ({
        display: 'none',
    }),

    indicatorsContainer: (provided, state) => ({
        ...provided,
        height: '31px',
    }),

    option: (provided, { data, isDisabled, isFocused, isSelected }) => ({
        ...provided,
        backgroundColor: isFocused ? 'lavender' : '#fff',
        color: 'hsl(219, 11%, 25%)',
        minHeight: '30px',
        height: '30px'
    }),
};

export default ({ name, placeholder, options, onChange, defaultValue, disabled }) =>
(<Select styles={customStyles}
    name={name}
    placeholder={placeholder}
    options={options}
    onChange={onChange}
    defaultValue={defaultValue}
    disabled={disabled}
/>
);

