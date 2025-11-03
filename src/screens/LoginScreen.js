import { useContext, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import LocusLogo from '../components/LocusLogo'; // Supondo que seu logo está em componentes
import PrimaryButton from '../components/PrimaryButton'; // Usaremos um botão primário padronizado
import { AuthContext } from '../context/AuthContext';
import { COLORS, FONTS, SIZES } from '../theme';

const LoginScreen = ({ navigation }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useContext(AuthContext);

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert('Erro', 'Por favor, preencha todos os campos.');
      return;
    }
    setLoading(true);
    try {
      await login(email, password);
      // A navegação será tratada pelo AuthContext
    } catch (error) {
      Alert.alert('Erro no Login', 'E-mail ou senha inválidos.');
      console.log(error);
    }
    setLoading(false);
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.container}>
        <View style={styles.innerContainer}>
          <LocusLogo style={styles.logo} />

          <Text style={styles.title}>Login</Text>

          <View style={styles.inputContainer}>
            <Text style={styles.label}>E-mail</Text>
            <TextInput
              style={styles.input}
              placeholder="seuemail@exemplo.com"
              placeholderTextColor={COLORS.placeholder}
              value={email}
              onChangeText={setEmail}
              keyboardType="email-address"
              autoCapitalize="none"
            />
          </View>

          <View style={styles.inputContainer}>
            <Text style={styles.label}>Senha</Text>
            <TextInput
              style={styles.input}
              placeholder="Sua senha"
              placeholderTextColor={COLORS.placeholder}
              value={password}
              onChangeText={setPassword}
              secureTextEntry
            />
          </View>

          <PrimaryButton
            title={loading ? <ActivityIndicator color={COLORS.white} /> : "Login"}
            onPress={handleLogin}
            disabled={loading}
          />

          <TouchableOpacity
            style={styles.signupButton}
            onPress={() => navigation.navigate('Signup')}>
            <Text style={styles.signupText}>
              Não tem uma conta? <Text style={styles.signupLink}>Cadastre-se</Text>
            </Text>
          </TouchableOpacity>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  innerContainer: {
    paddingHorizontal: SIZES.padding,
    alignItems: 'center',
  },
  logo: {
    marginBottom: SIZES.padding * 2,
    // Ajuste o tamanho do seu logo aqui se necessário
  },
  title: {
    ...FONTS.h2,
    color: COLORS.text,
    marginBottom: SIZES.padding,
  },
  inputContainer: {
    width: '100%',
    marginBottom: SIZES.base * 2,
  },
  label: {
    ...FONTS.body4,
    color: COLORS.textSecondary,
    marginBottom: SIZES.base,
    marginLeft: SIZES.base,
  },
  input: {
    backgroundColor: COLORS.lightGray,
    borderWidth: 1,
    borderColor: COLORS.border,
    borderRadius: SIZES.radius,
    paddingVertical: SIZES.base * 1.5,
    paddingHorizontal: SIZES.base * 2,
    ...FONTS.body3,
    color: COLORS.text,
  },
  signupButton: {
    marginTop: SIZES.padding,
  },
  signupText: {
    ...FONTS.body4,
    color: COLORS.textSecondary,
  },
  signupLink: {
    color: COLORS.primary,
    fontFamily: 'Roboto-Bold',
  },
});

export default LoginScreen;