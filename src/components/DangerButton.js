import { StyleSheet, Text, TouchableOpacity } from 'react-native';
import { COLORS, FONTS, SIZES } from '../theme';

const DangerButton = ({ title, onPress, disabled, style }) => {
  return (
    <TouchableOpacity
      style={[
        styles.button,
        disabled && styles.disabled,
        style,
      ]}
      onPress={onPress}
      disabled={disabled}>
      <Text style={styles.text}>{title}</Text>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  button: {
    backgroundColor: COLORS.danger, // Cor principal é a de perigo
    width: '100%',
    padding: SIZES.base * 2,
    borderRadius: SIZES.radius,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    ...FONTS.h4,
    color: COLORS.white,
  },
  disabled: {
    backgroundColor: COLORS.placeholder,
  },
});

export default DangerButton;