 
# Application Java Swing pour la Simulation de Clustering en WSN avec LEACH

## Configuration Système et Installation
- Java 11 est requis pour exécuter cette application.

## Données de Simulation
- Énergie Initiale par Noeud: 33 joules
- Énergie Initiale pour un Cluster Head: 55 joules

## Formule de Seuil
Le seuil est calculé selon la formule suivante :

seuil = (P / ((1 - P) * (roundNumber % (1 / P)))) * (node.getEnergy() / E_MAX );

## Configuration des Clusters
- Le nombre de clusters est fixé à 4 pour des raisons d'affichage, mais peut être étendu au besoin.
- L'utilisateur doit saisir le nombre de noeuds pour chaque cluster.
- Les informations de chaque noeud peuvent être affichées en cliquant dessus.
- Les noeuds peuvent être déplacés dans la fenêtre.

## Sections de l'Interface Utilisateur
- **Dernier Round:** Fenêtre à droite pour afficher les informations du dernier round.
- **Historique:** Section en bas pour visualiser les informations de tous les rounds.
- **Graphique:** Bouton "Graphe" pour afficher le graphique de variation de l'énergie totale.

## Simulation
- Pour Simuler un (01) Round appuyer sur bouton "Simulate", continuer a appuyer pour dérouler 
   plus de Rounds. 