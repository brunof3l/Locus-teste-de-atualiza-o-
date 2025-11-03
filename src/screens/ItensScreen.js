import firestore from '@react-native-firebase/firestore';
import { useEffect, useMemo, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  FlatList,
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import Icon from 'react-native-vector-icons/Feather';
import { COLORS, FONTS, SIZES } from '../theme';

// Este é o novo Card de Item, definido aqui para simplicidade
const ItemCard = ({ item, onPress }) => (
  <View style={styles.card}>
    <View style={styles.cardTextContainer}>
      <Text style={styles.cardTitle}>{item.nome}</Text>
      <Text style={styles.cardInfo}>Patrimônio: {item.patrimonio || 'N/A'}</Text>
      <Text style={styles.cardInfo}>Setor: {item.setor || 'N/A'}</Text>
    </View>
    <TouchableOpacity style={styles.cardButton} onPress={onPress}>
      <Text style={styles.cardButtonText}>Ver Detalhes</Text>
    </TouchableOpacity>
  </View>
);

const ItensScreen = ({ navigation }) => {
  const [loading, setLoading] = useState(true);
  const [items, setItems] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');

  // Busca os itens do Firestore
  useEffect(() => {
    const subscriber = firestore()
      .collection('items')
      .orderBy('nome', 'asc')
      .onSnapshot(
        querySnapshot => {
          const list = [];
          querySnapshot.forEach(doc => {
            list.push({
              id: doc.id,
              ...doc.data(),
            });
          });
          setItems(list);
          setLoading(false);
        },
        error => {
          console.error(error);
          Alert.alert("Erro", "Não foi possível carregar os itens.");
          setLoading(false);
        },
      );

    return () => subscriber();
  }, []);

  // Filtra os itens com base na busca
  const filteredItems = useMemo(() => {
    if (!searchQuery) {
      return items;
    }
    return items.filter(
      item =>
        item.nome.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.patrimonio.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.setor.toLowerCase().includes(searchQuery.toLowerCase()),
    );
  }, [items, searchQuery]);

  if (loading) {
    return (
      <SafeAreaView style={styles.safeArea}>
        <ActivityIndicator
          size="large"
          color={COLORS.primary}
          style={{ flex: 1 }}
        />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.safeArea}>
      {/* 1. Cabeçalho da Tela */}
      <View style={styles.screenHeader}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Icon name="chevron-left" size={30} color={COLORS.primary} />
        </TouchableOpacity>
        <Text style={styles.screenTitle}>Itens</Text>
        <View style={{ width: 30 }} /> 
      </View>

      {/* 2. Barra de Busca */}
      <View style={styles.searchContainer}>
        <Icon
          name="search"
          size={20}
          color={COLORS.placeholder}
          style={styles.searchIcon}
        />
        <TextInput
          style={styles.searchInput}
          placeholder="Buscar por nome, patrimônio, setor..."
          placeholderTextColor={COLORS.placeholder}
          value={searchQuery}
          onChangeText={setSearchQuery}
        />
      </View>

      {/* 3. Lista de Itens */}
      <FlatList
        data={filteredItems}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <ItemCard
            item={item}
            onPress={() => navigation.navigate('DetalhesItem', { itemId: item.id })}
          />
        )}
        contentContainerStyle={styles.listContainer}
        ListEmptyComponent={
          <Text style={styles.emptyText}>Nenhum item encontrado.</Text>
        }
      />

      {/* 4. Botão Flutuante (FAB) para Adicionar Item */}
      <TouchableOpacity
        style={styles.fab}
        onPress={() => navigation.navigate('CadastroItem')}>
        <Icon name="plus" size={28} color={COLORS.white} />
      </TouchableOpacity>
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
  // Busca
  searchContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: COLORS.lightGray,
    borderRadius: SIZES.radius,
    margin: SIZES.padding,
    paddingHorizontal: SIZES.base * 2,
  },
  searchIcon: {
    marginRight: SIZES.base,
  },
  searchInput: {
    flex: 1,
    ...FONTS.body3,
    paddingVertical: SIZES.base * 1.5,
    color: COLORS.text,
  },
  // Lista
  listContainer: {
    paddingHorizontal: SIZES.padding,
    paddingBottom: 100, // Espaço para o FAB
  },
  emptyText: {
    ...FONTS.body3,
    color: COLORS.textSecondary,
    textAlign: 'center',
    marginTop: SIZES.padding * 2,
  },
  // Card
  card: {
    backgroundColor: COLORS.card,
    borderRadius: SIZES.radius,
    padding: SIZES.base * 2,
    marginBottom: SIZES.base * 2,
    // Sombra
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardTextContainer: {
    marginBottom: SIZES.base * 2,
  },
  cardTitle: {
    ...FONTS.h3,
    color: COLORS.text,
    marginBottom: SIZES.base,
  },
  cardInfo: {
    ...FONTS.body4,
    color: COLORS.textSecondary,
    lineHeight: 20,
  },
  cardButton: {
    backgroundColor: COLORS.lightGray,
    borderRadius: SIZES.radius,
    paddingVertical: SIZES.base,
    alignItems: 'center',
  },
  cardButtonText: {
    ...FONTS.h4,
    color: COLORS.primary,
    fontSize: 14,
  },
  // FAB
  fab: {
    position: 'absolute',
    bottom: SIZES.padding,
    right: SIZES.padding,
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: COLORS.primary,
    justifyContent: 'center',
    alignItems: 'center',
    // Sombra
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 5,
    elevation: 8,
  },
});

export default ItensScreen;