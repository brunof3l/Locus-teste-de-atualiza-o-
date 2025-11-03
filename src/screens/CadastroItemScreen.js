import firestore from '@react-native-firebase/firestore';
import { useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import Icon from 'react-native-vector-icons/Feather';
import PrimaryButton from '../components/PrimaryButton';
import { COLORS, FONTS, SIZES } from '../theme';

const CadastroItemScreen = ({ navigation, route }) => {
  // Opcionalmente, podemos receber um patrimônio via QR Code
  const patrimonioInicial = route.params?.patrimonio || '';

  const [nome, setNome] = useState('');
  const [patrimonio, setPatrimonio] = useState(patrimonioInicial);
  const [setor, setSetor] = useState('');
  const [descricao, setDescricao] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSave = async () => {
    if (!nome || !patrimonio || !setor) {
      Alert.alert('Erro', 'Por favor, preencha Nome, Patrimônio e Setor.');
      return;
    }
    setLoading(true);
    try {
      await firestore().collection('items').add({
        nome,
        patrimonio,
        setor,
        descricao,
        createdAt: firestore.FieldValue.serverTimestamp(),
      });
      Alert.alert('Sucesso', 'Item cadastrado com sucesso!');
      navigation.goBack();
    } catch (error) {
      console.error(error);
      Alert.alert('Erro', 'Não foi possível cadastrar o item.');
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      {/* 1. Cabeçalho da Tela */}
      <View style={styles.screenHeader}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Icon name="chevron-left" size={30} color={COLORS.primary} />
        </TouchableOpacity>
        <Text style={styles.screenTitle}>Cadastrar Item</Text>
        <View style={{ width: 30 }} />
      </View>

      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={{ flex: 1 }}>
        <ScrollView style={styles.container}>
          {/* Campo Nome */}
          <View style={styles.inputContainer}>
            <Text style={styles.label}>Nome do Item</Text>
            <TextInput
              style={styles.input}
              placeholder="Ex: Cadeira de escritório"
              placeholderTextColor={COLORS.placeholder}
              value={nome}
              onChangeText={setNome}
            />
          </View>

          {/* Campo Patrimônio */}
          <View style={styles.inputContainer}>
            <Text style={styles.label}>Patrimônio (ID)</Text>
            <TextInput
              style={styles.input}
              placeholder="Ex: 123456"
              placeholderTextColor={COLORS.placeholder}
              value={patrimonio}
              onChangeText={setPatrimonio}
              keyboardType="number-pad"
            />
          </View>

          {/* Campo Setor */}
          <View style={styles.inputContainer}>
            <Text style={styles.label}>Setor</Text>
            <TextInput
              style={styles.input}
              placeholder="Ex: TI, Administração"
              placeholderTextColor={COLORS.placeholder}
              value={setor}
              onChangeText={setSetor}
            />
          </View>

          {/* Campo Descrição */}
          <View style={styles.inputContainer}>
            <Text style={styles.label}>Descrição (Opcional)</Text>
            <TextInput
              style={[styles.input, styles.textArea]}
              placeholder="Detalhes sobre o item..."
              placeholderTextColor={COLORS.placeholder}
              value={descricao}
              onChangeText={setDescricao}
              multiline
              numberOfLines={4}
            />
          </View>

          <PrimaryButton
            title={loading ? <ActivityIndicator color={COLORS.white} /> : 'Salvar Item'}
            onPress={handleSave}
            disabled={loading}
            style={{ marginTop: SIZES.padding }}
          />
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  // Cabeçalho
  screenHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: SIZES.padding,
    paddingVertical: SIZES.base,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.border,
  },
  screenTitle: {
    ...FONTS.h3,
    color: COLORS.text,
  },
  // Formulário
  container: {
    flex: 1,
    padding: SIZES.padding,
  },
  inputContainer: {
    width: '100%',
    marginBottom: SIZES.base * 2.5,
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
  textArea: {
    height: 120,
    textAlignVertical: 'top', // Para Android
    paddingTop: SIZES.base * 1.5, // Para iOS
  },
});

export default CadastroItemScreen;