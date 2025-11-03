import { Dimensions } from 'react-native';

const { width, height } = Dimensions.get('window');

export const COLORS = {
  // Cores Base
  primary: '#4A90E2', // Um azul moderno, como visto nos botões
  background: '#FFFFFF', // Fundo branco limpo
  card: '#FFFFFF', // Cards brancos com sombra

  // Cores de Texto
  text: '#333333', // Texto principal (preto suave)
  textSecondary: '#666666', // Texto secundário (cinza)
  placeholder: '#AAAAAA', // Cor do placeholder em inputs
  white: '#FFFFFF',

  // Cores de Feedback
  danger: '#D0021B', // Vermelho para erros ou exclusão
  success: '#7ED321', // Verde para sucesso

  // Neutros
  border: '#E0E0E0', // Cor da borda para inputs
  lightGray: '#F5F5F5', // Um cinza bem claro para fundos de card ou divisões
};

export const SIZES = {
  // Tamanhos Globais
  base: 8,
  font: 14,
  radius: 12,
  padding: 24,

  // Tamanhos de Fonte
  h1: 30,
  h2: 24,
  h3: 18,
  h4: 16,
  body1: 30,
  body2: 22,
  body3: 16,
  body4: 14,

  // Dimensões do App
  width,
  height,
};

export const FONTS = {
  h1: { fontFamily: 'Roboto-Bold', fontSize: SIZES.h1, lineHeight: 36, color: COLORS.text },
  h2: { fontFamily: 'Roboto-Bold', fontSize: SIZES.h2, lineHeight: 30, color: COLORS.text },
  h3: { fontFamily: 'Roboto-Bold', fontSize: SIZES.h3, lineHeight: 22, color: COLORS.text },
  h4: { fontFamily: 'Roboto-Bold', fontSize: SIZES.h4, lineHeight: 22, color: COLORS.text },
  body1: { fontFamily: 'Roboto-Regular', fontSize: SIZES.body1, lineHeight: 36, color: COLORS.text },
  body2: { fontFamily: 'Roboto-Regular', fontSize: SIZES.body2, lineHeight: 30, color: COLORS.text },
  body3: { fontFamily: 'Roboto-Regular', fontSize: SIZES.body3, lineHeight: 22, color: COLORS.text },
  body4: { fontFamily: 'Roboto-Regular', fontSize: SIZES.body4, lineHeight: 22, color: COLORS.text },
};

const appTheme = { COLORS, SIZES, FONTS };

export default appTheme;