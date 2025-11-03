import firestore from '@react-native-firebase/firestore';
import { useContext, useEffect, useState } from 'react';
import {
  Alert,
  SafeAreaView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import Icon from 'react-native-vector-icons/Feather'; // Usando Feather Icons (instale se não tiver)
import LocusLogo from '../components/LocusLogo'; // Reutilizando seu logo
import { AuthContext } from '../context/AuthContext';
import { COLORS, FONTS, SIZES } from '../theme';

// Você precisará instalar: npm install react-native-vector-icons
// e seguir as instruções de instalação nativa (principalmente no Android/app/build.gradle)

const HomeScreen = ({ navigation }) => {
  const { user, logout } = useContext(AuthContext);
  const [itemCount, setItemCount] = useState(0);
  const [loading, setLoading] = useState(true);

  // Efeito para buscar o *contador* de itens
  useEffect(() => {
    const subscriber = firestore()
      .collection('items')
      .onSnapshot(
        querySnapshot => {
          setItemCount(querySnapshot.size); // Apenas contamos os documentos
          setLoading(false);
        },
        error => {
          console.error(error);
          Alert.alert("Erro", "Não foi possível carregar o número de itens.");
          setLoading(false);
        },
      );

    // Encerra o listener ao desmontar
    return () => subscriber();
  }, []);

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error(error);
      Alert.alert("Erro", "Não foi possível fazer logout.");
    }
  };

  // Pega o primeiro nome do usuário
  const userName = user?.displayName ? user.displayName.split(' ')[0] : 'Usuário';

  return (
    <SafeAreaView style={styles.safeArea}>
      {/* 1. Cabeçalho */}
      <View style={styles.header}>
        <LocusLogo height={30} /> 
        <TouchableOpacity onPress={handleLogout}>
          <Icon name="log-out" size={24} color={COLORS.textSecondary} />
        </TouchableOpacity>
      </View>

      <View style={styles.container}>
        {/* 2. Saudação */}
        <Text style={styles.greeting}>Olá, {userName}</Text>

        {/* 3. Botões de Ação */}
        <TouchableOpacity
          style={[styles.actionCard, { backgroundColor: COLORS.primary }]}
          onPress={() => navigation.navigate('Scanner')}>
          <Icon name="maximize" size={28} color={COLORS.white} style={styles.cardIcon} />
          <Text style={styles.actionCardText}>Escanear QR Code</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.actionCard, { backgroundColor: COLORS.lightGray }]}
          onPress={() => navigation.navigate('Itens')}>
          <Icon name="list" size={28} color={COLORS.text} style={styles.cardIcon} />
          <Text style={[styles.actionCardText, { color: COLORS.text }]}>
            Ver Itens
          </Text>
        </TouchableOpacity>

        {/* 4. Card do Contador */}
        <View style={styles.countCard}>
          <Text style={styles.countLabel}>Itens Cadastrados</Text>
          {loading ? (
            <ActivityIndicator size="large" color={COLORS.primary} />
          ) : (
            <Text style={styles.countNumber}>{itemCount}</Text>
          )}
        </View>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: SIZES.padding,
    paddingVertical: SIZES.base * 2,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.border,
  },
  container: {
    flex: 1,
    padding: SIZES.padding,
  },
  greeting: {
    ...FONTS.h2,
    color: COLORS.text,
    marginBottom: SIZES.padding,
  },
  actionCard: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: SIZES.padding,
    borderRadius: SIZES.radius,
    marginBottom: SIZES.base * 2,
    // Sombra para elevação
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardIcon: {
    marginRight: SIZES.base * 2,
  },
  actionCardText: {
    ...FONTS.h3,
    color: COLORS.white,
  },
  countCard: {
    backgroundColor: COLORS.card,
    borderRadius: SIZES.radius,
    padding: SIZES.padding,
    alignItems: 'center',
    marginTop: SIZES.base * 2,
    // Sombra
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  countLabel: {
    ...FONTS.h3,
    color: COLORS.textSecondary,
    marginBottom: SIZES.base,
  },
  countNumber: {
    ...FONTS.h1,
    fontSize: 48,
    color: COLORS.primary,
    fontFamily: 'Roboto-Bold',
  },
});

export default HomeScreen;