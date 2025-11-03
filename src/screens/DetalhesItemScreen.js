import firestore from '@react-native-firebase/firestore';
import { useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import Icon from 'react-native-vector-icons/Feather';
import DangerButton from '../components/DangerButton'; // Criaremos este componente
import PrimaryButton from '../components/PrimaryButton';
import { COLORS, FONTS, SIZES } from '../theme';

const DetalhesItemScreen = ({ navigation, route }) => {
  const { itemId } = route.params;
  const [item, setItem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    const subscriber = firestore()
      .collection('items')
      .doc(itemId)
      .onSnapshot(
        documentSnapshot => {
          if (documentSnapshot.exists) {
            setItem({
              id: documentSnapshot.id,
              ...documentSnapshot.data(),
            });
          } else {
            Alert.alert('Erro', 'Item não encontrado.');
            navigation.goBack();
          }
          setLoading(false);
        },
        error => {
          console.error(error);
          Alert.alert('Erro', 'Não foi possível carregar o item.');
          navigation.goBack();
        },
      );

    return () => subscriber();
  }, [itemId, navigation]);

  const handleDelete = () => {
    Alert.alert(
      'Confirmar Exclusão',
      `Tem certeza que deseja excluir "${item.nome}"? Esta ação não pode ser desfeita.`,
      [
        { text: 'Cancelar', style: 'cancel' },
        {
          text: 'Excluir',
          style: 'destructive',
          onPress: async () => {
            setDeleting(true);
            try {
              await firestore().collection('items').doc(itemId).delete();
              Alert.alert('Sucesso', 'Item excluído.');
              navigation.goBack();
            } catch (error) {
              console.error(error);
              Alert.alert('Erro', 'Não foi possível excluir o item.');
              setDeleting(false);
            }
          },
        },
      ],
    );
  };

  // Função para formatar data (opcional)
  const formatDate = timestamp => {
    if (!timestamp) return 'Data indisponível';
    return new Date(timestamp.toDate()).toLocaleDateString('pt-BR');
  };

  if (loading) {
    return (
      <SafeAreaView style={styles.safeArea}>
        <ActivityIndicator
          size="large"
          color={COLORS.primary}
          style={styles.loading}
        />
      </SafeAreaView>
    );
  }

  if (!item) {
    return null; // Já foi tratado no useEffect
  }

  return (
    <SafeAreaView style={styles.safeArea}>
      {/* 1. Cabeçalho da Tela */}
      <View style={styles.screenHeader}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Icon name="chevron-left" size={30} color={COLORS.primary} />
        </TouchableOpacity>
        <Text style={styles.screenTitle} numberOfLines={1}>
          {item.nome}
        </Text>
        <View style={{ width: 30 }} />
      </View>

      <ScrollView style={styles.container}>
        {/* Card de Informações */}
        <View style={styles.infoCard}>
          <InfoRow label="Nome" value={item.nome} />
          <InfoRow label="Patrimônio" value={item.patrimonio} />
          <InfoRow label="Setor" value={item.setor} />
          <InfoRow
            label="Cadastrado em"
            value={formatDate(item.createdAt)}
          />
          <InfoRow
            label="Descrição"
            value={item.descricao || 'Nenhuma descrição.'}
            isLast
          />
        </View>

        {/* Botões de Ação */}
        <PrimaryButton
          title="Editar Item"
          onPress={() => navigation.navigate('EdicaoItem', { item: item })} // Supondo uma tela de edição
          style={{ marginBottom: SIZES.base * 2 }}
        />
        <DangerButton
          title={deleting ? <ActivityIndicator color={COLORS.white} /> : 'Excluir Item'}
          onPress={handleDelete}
          disabled={deleting}
        />
      </ScrollView>
    </SafeAreaView>
  );
};

// Componente auxiliar para as linhas de informação
const InfoRow = ({ label, value, isLast = false }) => (
  <View style={[styles.infoRow, isLast && styles.infoRowLast]}>
    <Text style={styles.infoLabel}>{label}</Text>
    <Text style={styles.infoValue}>{value}</Text>
  </View>
);

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  loading: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
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
    flex: 1,
    textAlign: 'center',
    marginHorizontal: SIZES.base,
  },
  // Conteúdo
  container: {
    flex: 1,
    padding: SIZES.padding,
  },
  infoCard: {
    backgroundColor: COLORS.card,
    borderRadius: SIZES.radius,
    padding: SIZES.padding,
    marginBottom: SIZES.padding,
    // Sombra
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  infoRow: {
    marginBottom: SIZES.base * 2,
    paddingBottom: SIZES.base * 2,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.border,
  },
  infoRowLast: {
    marginBottom: 0,
    paddingBottom: 0,
    borderBottomWidth: 0,
  },
  infoLabel: {
    ...FONTS.body4,
    color: COLORS.textSecondary,
    marginBottom: SIZES.base / 2,
  },
  infoValue: {
    ...FONTS.h4,
    color: COLORS.text,
    fontSize: 18,
  },
});

export default DetalhesItemScreen;