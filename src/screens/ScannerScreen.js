import { CameraView, useCameraPermissions } from 'expo-camera'; // Usando expo-camera
import { useEffect, useState } from 'react';
import { Alert, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import Icon from 'react-native-vector-icons/Feather';
import { COLORS, FONTS, SIZES } from '../theme';

const ScannerScreen = ({ navigation }) => {
  const [permission, requestPermission] = useCameraPermissions();
  const [scanned, setScanned] = useState(false);

  useEffect(() => {
    // Solicita permissão ao carregar a tela
    if (!permission?.granted) {
      requestPermission();
    }
  }, [permission, requestPermission]);

  const handleBarCodeScanned = ({ type, data }) => {
    setScanned(true);
    // Aqui, podemos decidir o que fazer com o dado.
    // Vamos supor que o QR code contém um número de patrimônio.
    // Podemos navegar para a tela de detalhes se o item existir,
    // ou para a tela de cadastro preenchendo o patrimônio.

    // Por enquanto, vamos navegar para CadastroItem com o dado
    Alert.alert(
      'QR Code Lido',
      `Dado: ${data}\n\nDeseja cadastrar um novo item com este patrimônio?`,
      [
        {
          text: 'Cancelar',
          onPress: () => setScanned(false), // Permite escanear novamente
          style: 'cancel',
        },
        {
          text: 'Cadastrar',
          onPress: () =>
            navigation.navigate('CadastroItem', { patrimonio: data }),
        },
      ],
    );
  };

  if (!permission) {
    // Permissões ainda estão carregando
    return <View />;
  }

  if (!permission.granted) {
    // Usuário negou a permissão
    return (
      <View style={styles.permissionContainer}>
        <Text style={styles.permissionText}>
          Precisamos de permissão para usar a câmera.
        </Text>
        <TouchableOpacity style={styles.permissionButton} onPress={requestPermission}>
          <Text style={styles.permissionButtonText}>Conceder Permissão</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.permissionButton} onPress={() => navigation.goBack()}>
          <Text style={styles.permissionButtonText}>Voltar</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={styles.safeArea}>
      <CameraView
        style={StyleSheet.absoluteFillObject}
        onBarcodeScanned={scanned ? undefined : handleBarCodeScanned}
        barcodeScannerSettings={{
          barcodeTypes: ['qr'], // Apenas QR codes
        }}
      />

      {/* 1. Cabeçalho da Tela (transparente) */}
      <View style={styles.screenHeader}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Icon name="chevron-left" size={30} color={COLORS.white} />
        </TouchableOpacity>
        <Text style={styles.screenTitle}>Escanear QR Code</Text>
        <View style={{ width: 30 }} />
      </View>

      {/* 2. Overlay (máscara) */}
      <View style={styles.overlay}>
        <Text style={styles.overlayText}>
          Aponte a câmera para o QR Code
        </Text>
        <View style={styles.scannerBox} />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#000', // Fundo preto para a câmera
  },
  // Cabeçalho
  screenHeader: {
    position: 'absolute',
    top: 40, // Ajuste para status bar
    left: 0,
    right: 0,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: SIZES.padding,
    paddingVertical: SIZES.base,
    zIndex: 10,
  },
  screenTitle: {
    ...FONTS.h3,
    color: COLORS.white,
    textShadowColor: 'rgba(0, 0, 0, 0.75)',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 2,
  },
  // Overlay do Scanner
  overlay: {
    ...StyleSheet.absoluteFillObject,
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0,0,0,0.3)', // Escurece a área externa
  },
  overlayText: {
    ...FONTS.h4,
    color: COLORS.white,
    textShadowColor: 'rgba(0, 0, 0, 0.75)',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 2,
    position: 'absolute',
    top: SIZES.height * 0.3,
  },
  scannerBox: {
    width: SIZES.width * 0.7,
    height: SIZES.width * 0.7,
    borderWidth: 3,
    borderColor: COLORS.white,
    borderRadius: SIZES.radius,
    backgroundColor: 'transparent',
  },
  // Permissão negada
  permissionContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: SIZES.padding,
    backgroundColor: COLORS.background,
  },
  permissionText: {
    ...FONTS.h3,
    textAlign: 'center',
    marginBottom: SIZES.padding,
  },
  permissionButton: {
    backgroundColor: COLORS.primary,
    padding: SIZES.base * 2,
    borderRadius: SIZES.radius,
    marginVertical: SIZES.base,
  },
  permissionButtonText: {
    ...FONTS.h4,
    color: COLORS.white,
  },
});

export default ScannerScreen;