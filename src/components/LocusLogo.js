import { StyleSheet, Text } from 'react-native';
import { COLORS, FONTS } from '../theme';

// Este é um logo de TEXTO. Se o seu for uma IMAGEM,
// substitua <Text> por <Image source={require('...')} />
const LocusLogo = ({ style, height }) => {
  return (
    <Text style={[styles.logo, style, height && { fontSize: height }]}>
      Locus
    </Text>
  );
};

const styles = StyleSheet.create({
  logo: {
    ...FONTS.h1,
    fontSize: 40,
    color: COLORS.primary,
    fontFamily: 'Roboto-Bold', // Use uma fonte bold se tiver
  },
});

export default LocusLogo;